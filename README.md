# Usask GeoGuessr

## Overview
    
Usask GeoGuessr is a program developped for CMPT370 Winter 2025 at the University of Saskatchewan by Matt Berry,
Jake Evertman, Adut Beny, Laaiba Shaikh and Ben Krysak. The program was inpsired by the popular webgame GeoGeussr,
where players are given a random location and after looking around must guess the location on a global map. We 
intended for this to be a variation of that game where the scope was limited to locations around the University
of Saskatchewan campus. It is both a learning tool for new or upcoming students to familiarize themselves with
central locations, and a way for experienced students and alumni to challenge themselves to locate more obscure
areas of campus. 

For more introductory information, please see Deliverable_0 on the wiki pages. For User Stories please see 
Deliverable_1. Deliverable_2 contains various diagrams, some of which are outdated. Deliverable_3 contains
our test plan and code reviews. Deliverable_4 has some final amendments to the test plan and Acceptance Test
validation from Deliverable_1.

## Installing & Running
    
To install the game, simply download the .jar file from the main branch of this git repository and run it.

No other dependencies should be needed.

## Architecture
    
Since this game is primarly event driven, relying on button presses and mouse clicks, we used a Model-View-Controlller
design pattern for the majority of program. This was at times hard to work under and created some added complexities
in the code, but overall made the program easier to develop as we added more and more pieces to it. The Model class
also served as the client polling two servers, one for Google Log-in Authentification (written in Python) 
and one for the Multiplayer real-time database (Google Firebase). 

UML Diagrams for both this architecture and other use cases can be found on the Wiki under Deliverable_3, although
some of these diagrams are depricated.

