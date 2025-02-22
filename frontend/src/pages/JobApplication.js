import Appbar from "../components/Appbar";
import * as React from 'react';
import Application from  "../components/Application"
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import ApplicationEndPoint from "../components/ApplicationEndPoint";


export default function JobApplication(){
    return (
        <>
        <Appbar/>
            <React.Fragment>
                <CssBaseline />
                <Container maxWidth="sm">
                    <Box sx={{ bgcolor: '#AFF9C9', height: '100%', p: 2 }} >

                        
                         
                        <ApplicationEndPoint/>
                    </Box>
                </Container>
            </React.Fragment>
        </>
       
    )
}