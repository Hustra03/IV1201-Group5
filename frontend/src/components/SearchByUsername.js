  import React, {useState,useContext} from 'react';
  import Box from '@mui/material/Box';
  import TextField from '@mui/material/TextField';
  import Button from '@mui/material/Button';
  import { isTokenExpired } from "./utils/TokenChecker";
  import { useNavigate } from "react-router-dom";

  import { Typography, Divider, CircularProgress, List, ListItem } from '@mui/material';

  import { AuthContext } from '../App';


/** 
 * This component allows users to search for a person by username, email, or PNR (Personal Number)
 * It fetches user data from an API and displays the results.
 */
export default function SearchByUsername() {

      const[search,setSearch] = useState("");
      const[result,setResult] = useState(null);

      const navigate = useNavigate();

      const { auth, setAuth } = useContext(AuthContext);

      // Get API URL from .env file
      const API_URL = process.env.REACT_APP_API_URL;
      // Function to determine the type of search input (PNR, email, or username)
      const parseSearch = () => {
          if(/^\d+-?\d+$/.test(search)) return "pnr";
          if(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(search)) return "email";
          else return "username";
      }

      // Function to handle search submission
    const handleSearch = (e) => {
        if(isTokenExpired(sessionStorage.getItem("token"))){ //if token has expired 
            setAuth({});
            sessionStorage.clear();
            alert("Your session has expired. Please log in again.");
            navigate("/login"); // Redirect to login page
        }
          e.preventDefault(); // Prevents page refresh


          const type = parseSearch();
          let param;
          if(type == "email"){
              param = new URLSearchParams({email: search});
          }else if (type == "pnr"){
              param = new URLSearchParams({pnr: search});
          } else{
              param = new URLSearchParams({username: search});
          }
          
          const url = new URL(`${API_URL}/person/findPerson`);
          

          // Add the params to the URL
          url.search = param.toString();
      
          console.log("Sending request with params:", url); // remove (just debug)
          
          fetch(url, {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              "Authorization": "Bearer "+auth.token 
            },
          })
          .then((response) => { 
            if (response.ok) {
                return response.json(); // Parse JSON if response is OK
            } else {                
              if(response.status==401) //This checks if the user was not authorized
              {
                  alert("You are not authorized to search for people");
                  throw new Error(`Unauthorized fetch`); 
              }
              else
              {
                  return response.text().then((errorText) => { 
                  throw new Error(`Failed to fetch: ${errorText}`); 
                });
            }
          }
          }) 
          .then((data) => {
              console.log(data); // Write data
              setResult(data)
          })
          .catch((error) => {
              console.error("Error Searching for:", error);
          });
      
      }
    return (
      <Box
        component="form"
        sx={{
          width: {xs: "70%", md: "40%"},
          height: "100%",
          justifySelf: "center",
          p: 2,
          borderRadius: 4,
          marginTop:2,
          gap: 2,
          overflow: "hidden",
          bgcolor: "#67E0A3",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          top: 0,

        }}
        
        noValidate
        autoComplete="off"
        onSubmit={handleSearch} // Allows Enter key to trigger search
      >
        <div>
          <Typography>Search for registered by Username, Email or Pnr</Typography>
          <TextField
            required
            id="standard-required"
            label="Search"
            
            variant="standard"
            value={search}
            onChange={(e) => setSearch(e.target.value)} // Save inputed data on change
          />
          <Button variant="outlined" onClick={handleSearch}>Submit</Button>
          <Typography variant='h6'>Results for {search}</Typography>               
          <Box sx={{boxShadow: 5, p: 2, borderRadius: 2, justifySelf: "center",}}>
              {result ? (
                  <Typography>{result.name} {result.surname}, E-mail: {result.email}, Pnr: {result.pnr}, Role: {result.role.name}, Username: {result.username} </Typography>
              ) : (
                  <Typography>Person not found</Typography>
              )}
          </Box>
        </div>
      </Box>
    );
  }
