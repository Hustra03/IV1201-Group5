import React, { useState, useEffect, useContext } from "react";
import {
  Container,
  TextField,
  Button,
  Typography,
  Paper,
  Box,
  List,
  ListItem,
  ListItemText,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  CircularProgress
} from "@mui/material";

import { AuthContext } from '../App';

export default function ApplicationEndPoint() {
    const [personId, setPersonId] = useState("");
    const [competenceProfiles, setCompetenceProfiles] = useState([]);
    const [competenceId, setCompetenceId] = useState("");
    const [yearsOfExperience, setYearsOfExperience] = useState("");
    const [availability, setAvailability] = useState([]);
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");
    const [applicationResult, setApplicationResult] = useState(null);
    const [competences, setCompetences] = useState([]);
    //-----------submit application variables----------
    const [competenceProfileIds, setCompetenceProfileIds] = useState([]);
    const [availabilityIds, setAvailabilityIds] = useState([]);
    //-------------Get Id based on Username-----------
    const [searchedPerson, setSearchedPerson] = useState();
    const [result, setResult] = useState();
    //--------------------Competences-------------

    //We load the authentication information from the context
    const { auth, setAuth } = useContext(AuthContext);
    

    // Get API URL from .env file
    const API_URL = process.env.REACT_APP_API_URL;
    
    // Fetch competence profiles
    const getCompetenceProfiles = async () => {
        const url = `${API_URL}/application/getAllCompetenceProfiles?personId=${personId}`;

        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${auth.token}`, 
            },
        })
        .then((response) => { 
            if (response.ok) {
                return response.json(); // Parse JSON if response is OK
            } else {
                return response.text().then((errorText) => { 
                    throw new Error(`Failed to fetch: ${errorText}`); 
                });
            }
        })
        .then((data) => {
            console.log("Received profile: ", data);
            setCompetenceProfiles(data); // Uppdaterar state med kompetenslistan
        })
        .catch((error) => {
            console.error("Error fetching competences:", error);
        });
    };

    //fetch id based on username or email or pnr
    //function to determin what param was entered
    const parseSearch = () => {
        if(/^\d+-?\d+$/.test(searchedPerson)) return "pnr";
        if(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(searchedPerson)) return "email";
        else return "username";
    }
    //fetch the id
    const fetchId = (e) => {
        e.preventDefault(); // Prevents page refresh

        const type = parseSearch();
        let param;
        if(type == "email"){
            param = new URLSearchParams({email: searchedPerson});
        }else if (type == "pnr"){
            param = new URLSearchParams({pnr: searchedPerson});
        } else{
            param = new URLSearchParams({username: searchedPerson});
        }
        
        const url = new URL(`${API_URL}/person/findPerson`);
        

        // Add the params to the URL
        url.search = param.toString();
    
        console.log("Sending request with params:", url); // remove (just debug)
        
        fetch(url, {
          method: "GET",
          headers: {
            
            "Content-Type": "application/json", 
            "Authorization": `Bearer ${auth.token}`, 
          },
        })
        .then((response) => { 
          if (response.ok) {
              return response.json(); // Parse JSON if response is OK
          } else {
              return response.text().then((errorText) => { 
                  throw new Error(`Failed to fetch: ${errorText}`); 
              });
          }
        }) 
        .then((data) => {
            console.log(data); // Write data
            setResult(data);
            setPersonId(data.id);
        })
        .catch((error) => {
            console.error("Error Searching for:", error);
        });
    
    };

    //function to fetch competences
    const fetchCompetences = () => {
        const url = `${API_URL}/translation/getStandardCompetences`;

        console.log("Fetching competences from:", url);

        fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${auth.token}`, 
            },
        })
        .then((response) => { 
            if (response.ok) {
                return response.json(); // Parse JSON if response is OK
            } else {
                return response.text().then((errorText) => { 
                    throw new Error(`Failed to fetch: ${errorText}`); 
                });
            }
        }) // Parsar svaret som JSON
        .then((data) => {
            console.log("Received competences:", data);
            setCompetences(data); // Uppdaterar state med kompetenslistan
            setCompetenceId(competences[0]?.competenceId || ""); 
        })
        .catch((error) => {
            console.error("Error fetching competences:", error);
        });
    };
    //Auto fetch competences to choose from
    useEffect(() => {
        fetchCompetences();
    },[]);

    //Auto fetch competences to choose from
    useEffect(() => {
        if (competences.length > 0) {
            console.log("triggered competese id")
            setCompetenceId(competences[0].competenceId); // Set initial competenceId
        }
    }, [competences]); // Trigger only when competences are updated


    // Create a new competence profile
    const createCompetenceProfile = async () => {
        const response = await fetch(`${API_URL}/application/createCompetenceProfile?personId=${personId}&competenceId=${competenceId}&yearsOfExperience=${yearsOfExperience}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${auth.token}`, 
        },
        });
        if (response.ok) {
        const data = await response.json();
        console.log("Creating profile with:", { competenceId, personId, yearsOfExperience });
        getCompetenceProfiles();

        alert("Competence profile created successfully!");
        } else {
        alert("Failed to create competence profile");
        }
    };

    // Fetch availability periods
    const getAvailability = async () => {
        const response = await fetch(`${API_URL}/application/getAllAvailability?personId=${personId}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${auth.token}`, 
                },
            }
        );
        if (response.ok) {
        setAvailability(await response.json());
        } else {
        console.log("Failed to fetch availability periods");
        }
    };

    useEffect(() => {
        if(personId !== ""){
            getAvailability();
        }
    }, [personId])

    useEffect(() => {
        if(personId !== ""){
            getCompetenceProfiles();
        }
    }, [personId])

    // Create a new availability period
    const createAvailability = async () => {
        const response = await fetch(`${API_URL}/application/createAvailability?personId=${personId}&fromDate=${fromDate}&toDate=${toDate}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${auth.token}`, 
        },
        });
        if (response.ok) {
        const data = await response.json();
        console.log("Creating availability with:", data);
        getAvailability();

        alert("Availability period created successfully!");
        } else {
        alert("Failed to create availability");
        }
    };

    //-------------Language translation----------------------
    const [language, setLanguage] = useState("");
    const [translations, setTranslations] = useState([]);
    const [languages, setLanguages] = useState([]);
    
    const fetchLanguages = async () => {
        try {
        const response = await fetch(`${API_URL}/translation/getLanguages`);
        if (!response.ok) throw new Error("Failed to fetch languages");
        const data = await response.json();
        setLanguages(data);
        setLanguage("english")
        } catch (error) {
        console.error(error);
        }
    };
    
    const fetchCompetenceTranslations = async () => {
        if (!language) return;
        console.log("language is set");
        try {
            const url = `${API_URL}/translation/getCompetenceTranslation?language=${language}`;
            fetch(url, {
                method: "GET",
                headers: {
                        // Send as form data, to comply with us using @Requestparam in controller
                    "Content-Type": "application/x-www-form-urlencoded", 
                },
            })
            .then((response) => { 
                if (response.ok) {
                    return response.json(); // Parse JSON if response is OK
                } else {
                    return response.text().then((errorText) => { 
                        throw new Error(`Failed to fetch: ${errorText}`); 
                    });
                }
            }) // Parse response as text
            .then((data) => {
                console.log(data); // Write data
                setTranslations(data);
            })
            .catch((error) => {
                console.error("Error adding applicant:", error);
            });
            
        } catch (error) {
            console.error(error);
            setTranslations([]);
        }
    };

    useEffect(() => {
        fetchLanguages();
    }, [])

    useEffect(() => {
        fetchCompetenceTranslations();
    }, [language])

    //-------------submit application---------------------
    const submitApplication = async () => {
        if (!personId || availability.length === 0 || competenceProfiles.length === 0) {
            console.error("Missing required fields.");
            alert("Please fill in competences and availability")
            return;
        }
    
        const requestBody = {
            personId: personId,
            availabilityIds: availabilityIds, // Assuming this is an array of selected availability IDs
            competenceProfileIds: competenceProfileIds // Assuming this is an array of created competence profile IDs
        };
    
        try {
            const response = await fetch(`${API_URL}/application/submitApplication`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${auth.token}`, 
                },
                body: JSON.stringify(requestBody),
            });
    
            if (!response.ok) throw new Error("Failed to submit application");
    
            const data = await response.json();
            console.log("Application submitted successfully:", data);
            alert("Application submitted successfully!");
        } catch (error) {
            console.error("Error submitting application:", error);
        }
    };
    //------------select competneces and availability-----------
    // handle the selection of multiple profiles
    const handleSelectCompetence = (selectedID) => {
        console.log("the selected id is: " + selectedID);
        const selectedCompetence = competenceProfiles.find((competenceProfile) => competenceProfile.competenceProfileId === selectedID);
        if (selectedCompetence){
            if(competenceProfileIds.find((profile) => profile === selectedCompetence.competenceProfileId)){  // if it exist we deselect it
                console.log("it already exists")
                setCompetenceProfileIds((prev) => prev.filter((profile) => profile !== selectedCompetence.competenceProfileId));
            }else { //if it dont exist we append

                setCompetenceProfileIds((prev) => [...prev, selectedCompetence.competenceProfileId]);
                console.log(competenceProfileIds);
            }
        } else {
            alert("faild to select competence");
        }
    };
    //handle selection of availability
    const handleSelectAvailability = (selectedID) => {
        console.log("the selected id is: " + selectedID);
        const selectedAvailability = availability.find((availabilityProfile) => availabilityProfile.availabilityId === selectedID);
        if (selectedAvailability){
            if(availabilityIds.find((profile) => profile === selectedAvailability.availabilityId)){  // if it exist we deselect it
                console.log("it already exists")
                setAvailabilityIds((prev) => prev.filter((profile) => profile !== selectedAvailability.availabilityId));
            }else { //if it dont exist we append

                setAvailabilityIds((prev) => [...prev, selectedAvailability.availabilityId]);
                console.log(availabilityIds);
            }
        } else {
            alert("faild to select availability");
        }
    };
    
    return (
            <Container maxWidth="md" sx={{ 
                marginTop: "10px",
                p: 4,
                borderRadius: 4,
                bgcolor: "#AFF9C9",
            }}>

            {/* Language Selector */}
            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Select Language</Typography>
                <FormControl fullWidth margin="dense">
                    <Select value={language} onChange={(e) => setLanguage(e.target.value)} displayEmpty>
                        <MenuItem value="" disabled>Select Language</MenuItem>
                        {languages.map((lang) => (
                            <MenuItem key={lang.languageId} value={lang.languageName}>{lang.languageName}</MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </Paper>

            {/* new enter username instead of id */}
            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Competence Profiles</Typography>
                <TextField label="username, email, pnr" value={searchedPerson} onChange={(e) => setSearchedPerson(e.target.value)} fullWidth />
                <Button variant="contained" color="primary" onClick={fetchId} style={{ marginTop: "10px" }}>
                    Get id
                </Button>
                <Typography>{personId} </Typography>
                <List>
                    {
                    
                    /*competenceProfiles.map((cp, index) => {
                        return (
                            <ListItem key={index}>
                                <ListItemText
                                    primary={`Competence: ${translations[cp.competenceDTO.competenceId -1] ? translations[cp.competenceDTO.competenceId -1].translation : "Unknown"}, Experience: ${cp.yearsOfExperience} years`}
                                />
                            </ListItem>
                        );
                    })*/}
                </List>
            </Paper>
            
            
            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Competence Profiles</Typography>
                <TextField label="Person ID" value={personId} onChange={(e) => setPersonId(e.target.value)} fullWidth />
                <Button variant="contained" color="primary" onClick={getCompetenceProfiles} style={{ marginTop: "10px" }}>
                    Get Competence Profiles
                </Button>
                <List>
                    {competenceProfiles.map((cp, index) => {
                        return (
                            <ListItem key={index}>
                                <ListItemText
                                    primary={`Competence: ${translations[cp.competenceDTO.competenceId -1] ? translations[cp.competenceDTO.competenceId -1].translation : "Unknown"}, Experience: ${cp.yearsOfExperience} years`}
                                />
                                <Button variant="contained" value={cp.competenceProfileId} onClick={() => handleSelectCompetence(cp.competenceProfileId)} sx={{
                                    transform: competenceProfileIds.includes(cp.competenceProfileId) ? "translateY: 2px" : "none",
                                    bgcolor: competenceProfileIds.includes(cp.competenceProfileId) ? "primary.main" : "grey",
                                }}>Select</Button>
                            </ListItem>
                        );
                    })}
                </List>
            </Paper>

            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Create Competence Profile</Typography>
                <FormControl fullWidth margin="dense">
                    {personId !== "" && competences ? (
                        competences.length > 0 ? (
                            
                            <Select value={competenceId} onChange={(e) => setCompetenceId(e.target.value)}>
                                {competences.map((comp) => (
                                    <MenuItem key={comp.competenceId} value={comp.competenceId}>
                                         {translations.find(t => t.competence.competenceId === comp.competenceId)?.translation || "Unknown"}
                                    </MenuItem>
                                ))}
                            </Select>
                        ) : (
                            <CircularProgress />  // Show loading indicator if no competences are available
                        )
                    ) : (
                        <Typography variant="h8" sx={{color: "red", m: 1}}>Please set person id first</Typography>
                    )}
                </FormControl>
                <TextField label="Years of Experience" value={yearsOfExperience} onChange={(e) => setYearsOfExperience(e.target.value)} fullWidth />
                <Button variant="contained" color="secondary" onClick={createCompetenceProfile} style={{ marginTop: "10px" }}>
                Create Profile
                </Button>
            </Paper>

            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Availability</Typography>
                <Button variant="contained" color="primary" onClick={getAvailability}>
                Get Availability Periods
                </Button>
                
                {personId !== "" ? (

                    availability.length > 0 ? (
                        <List>
                        {availability.map((a, index) => (
                            <ListItem key={index}>
                            <ListItemText primary={`From: ${a.fromDate}, To: ${a.toDate}`} />
                            <Button variant="contained" value={a.availabilityId} onClick={() => handleSelectAvailability(a.availabilityId)} sx={{
                                transform: availabilityIds.includes(a.availabilityId) ? "translateY: 2px" : "none",
                                bgcolor: availabilityIds.includes(a.availabilityId) ? "primary.main" : "grey",
                            }}>Select</Button>
                            </ListItem>
                        ))}
                        </List>
                    ) : (
                        <Typography variant="h8">No registered availability periods</Typography>
                    )
                ) : (
                    <Typography variant="h8" sx={{color: "red", m: 1}}>Please set person id first</Typography>
                )
                }
            </Paper>

            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                <Typography variant="h6">Create Availability</Typography>
                {personId !== "" ? (
                    <>
                        <Typography variant="h8">From date</Typography>
                        <TextField type="date"  value={fromDate} onChange={(e) => setFromDate(e.target.value)} fullWidth />
                        <Typography variant="h8">To date</Typography>
                        <TextField type="date"  value={toDate} onChange={(e) => setToDate(e.target.value)} fullWidth />
                        <Button variant="contained" color="secondary" onClick={createAvailability} style={{ marginTop: "10px" }}>
                        Create Availability
                        </Button>
                    </>
                ) : (
                    <Typography variant="h8" sx={{color: "red", m: 1}}>Please set person id first</Typography>
                )}
            </Paper>

            <Paper elevation={3} sx={{ padding: "20px", marginBottom: "20px", bgcolor: "#67E0A3" }}>
                
                <Button variant="contained" color="primary" onClick={submitApplication}>
                Submit Application
                </Button>
                <Typography>{competenceProfileIds /*just for debug and testing (remove) */}</Typography> 
                <Typography>{availabilityIds /*just for debug and testing (remove) */}</Typography>
            </Paper>
            </Container>
        
    );
};

