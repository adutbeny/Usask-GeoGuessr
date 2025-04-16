# Usask GeoGuessr

## Overview
    
Usask GeoGuessr is a program developed for CMPT370 Winter 2025 at the University of Saskatchewan by Matt Berry,
Jake Evertman, Adut Beny, Laaiba Shaikh and Ben Krysak. The program was inspired by the popular webgame GeoGuessr,
where players are given a random location and after looking around must guess the location on a global map. We 
intended for this to be a variation of that game where the scope was limited to locations around the University
of Saskatchewan campus. It is both a learning tool for new or upcoming students to familiarize themselves with
central locations, and a way for experienced students and alumni to challenge themselves to locate more obscure
areas of campus. 

## Gameplay Sample
![Gameplay Sample](Gameplay/Gameplay_Example.mp4)

## Installing & Running
    
To install and run the game, your own instances of google maps API, MySQL database are required to maintain scores and use the map. 
Fill in this information after creating newfile config.template under src/main/resources. Use the config.properties.template under resources to build your file.
Create a .jar file and run the application.

No other dependencies should be needed.

## Architecture
    
Since this game is primarly event driven, relying on button presses and mouse clicks, we used a Model-View-Controlller
design pattern for the majority of program. This was at times hard to work under and created some added complexities
in the code, but overall made the program easier to develop as we added more and more pieces to it. The Model class
also served as the client polling two servers, one for Google Log-in Authentification (written in Python) 
and one for the Multiplayer real-time database (Google Firebase). 


