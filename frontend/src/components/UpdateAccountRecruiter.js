    import React, { useState, useContext } from "react";
    import { TextField, Button, Container, Typography, Box } from "@mui/material";
    import { AuthContext } from '../App';
    import { isTokenExpired } from "./utils/TokenChecker";
    import { useNavigate } from "react-router-dom";

const RecruiterForm = () => {
        // Get API URL from .env file
    const API_URL = process.env.REACT_APP_API_URL;

    const [pnr, setPnr] = useState("");
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    const navigate = useNavigate();

    const { auth, setAuth } = useContext(AuthContext);

    const ResetEmail = (e) => {
        if(isTokenExpired(sessionStorage.getItem("token"))){ //if token has expired 
            setAuth({});
            sessionStorage.clear();
            alert("Your session has expired. Please log in again.");
            navigate("/login"); // Redirect to login page
        }
        e.preventDefault(); // Prevents page refresh

        const params = new URLSearchParams();
        params.append("pnr", pnr);
        params.append("email", email);
        
        const url = `${API_URL}/person/updateRecruiter?${params.toString()}`;
        
        console.log("Sending: ", url); // remove (just debug)
        
        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json", 
                "Authorization": "Bearer "+ auth.token
            },
            
        })
        .then((response) => { 
            if (response.ok) {
                return response.text(); // Parse JSON if response is OK
                
            } else {
                return response.text().then((errorText) => { 
                    throw new Error(`Failed to fetch: ${errorText}`); 
                });
            }
            }) 
        .then((data) => {
            console.log(data);
            
            setMessage(data);
        })
        .catch((error) => {
            console.error("Error:", error);
            alert("Reset token is incorrect or username is taken")
        });
        
    };

    return (
        <Container maxWidth="sm">
        <Typography variant="h4" color="white" gutterBottom>
            Recruiter Panel
        </Typography>
        <Box component="form" onSubmit={ResetEmail} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            <TextField label="New PNR" variant="outlined" value={pnr} onChange={(e) => setPnr(e.target.value)} required />
            <TextField label="New Email" variant="outlined" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <Button type="submit" variant="contained" color="primary">Update</Button>
        </Box>
        {message && <Typography variant="h6" color="white">{message}</Typography>}
        </Container>
    );
    };

    export default RecruiterForm;
