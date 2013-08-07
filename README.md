# PlayConf - App for first Play framework conference

Building a fictitious free playframework conference webapp that looks very similar to scaladays.org.
* Submit proposal
* Show selected sessions with speaker bio from database
* Register using Twitter OAuth
* Have a ticker that is reactively updated every time someone registers or proposes a session using Websocket


## Title: Building reactive web apps using Java and Play framework

Here are the list of lessons of the online course.

## Meet Play - ­ Reactive webframework 

This will introduce the Play Framework and the tools we are going to use to build the sample application.

* Introduce the objective of the course
* What is Play?
* What is a reactive programming?
* Why reactive programming is important?
* How is Play Reactive?
* Key features of Play ­ Hit refresh workflow, Type safety, built­in test framework, full IDE support in either Eclipse or IntelliJ. We will show that by generating a sample play project and make some changes to demonstrate the workflow
* Play architecture? ­ Stateless Web Tier, Non­blocking I/O, Built on Akka
* Teaser about the next episode

__Take away points__: reactive programming, architecture, tools, playframework features

## From zero to deploy in 15 mins
 
Build the first part of the use case and implement a basic sample with Scala Templates. This should wow the folks.

* Describe the PlayConf project
* Implement submit proposal usecase to show to perform POST operations
* Create form using the view helpers
* Submit the form
* Add domain models
* Save the data to mysql database using Ebean
* Add the pre­speakers info to the in­memory database using play evolutions (if we get time do it or else move it to next episode)
* Deploy the application to cloudbees

__Take away points__: mvc components, routes, view helpers, form, ebean, adding library dependencies, deploying to cloud.

## Making the app look pretty
 
This episode will introduce the HTML5 support of Play application. Integrate the html layout, Coffeescript and SCSS to the application.

* Add coffeescript assets
* Add scss assets
* Walk through how asset pipeline work in Play
* Difference between DEV vs. PROD
* Introduce webjars
* Introduce Play Global and add an custom error page
* Modify Scala templates, layout and Assets controller

__Take away points__: assets, asset compilation, asset error handling, asset minification, websocket, webjars

## Its now reactive

This episode is all about making the application reactive.

* Blocking vs. non­blocking
* Introduce functions and callbacks
* Introduce Future & Promise
* Add Websocket displays a note in UI when some submits a proposal.
* Introduce the play Json library
* Build a REST service that randomly returns an approved session that it pushed through websocket and displayed on the home page
* Go back and refactor the blocking database call from the second episode

__Take away points__: JSON, async programming, future composition, websocket

##  Using REST services
 
To show integration with external services we will use Twitter to register users for the conference.

* What is REST?
* Register users using Twitter
* Show the registered user in the message board.
* Show the effect of the user registration through some simulation.

__Take away points__: webservice client (WS client). Twitter, OAuth

##  Time to add some tests and DI
 
Focus on unit testing features of Playframework Actually we can really help here. Lots of confusion around how to test in play. Testing controllers, models etc.

* How to do dependency injection with Play (maybe a side note about using spring)
* Testing controllers with Async action
* Testing models that uses Ebean
* Testing views

__Take away points__: testing controllers and views, testing async actions, DI, global settings, test configuration
