showRegisteredUser = (json) ->
  article = $("#buzz").find("article:first").remove()
  $(article).find("#registeredUserPictureUrl").attr "src", json.pictureUrl
  $(article).find("#registerdUserName").html json.name
  $(article).find("#registerdUserTwitterId").html json.twitterId
  $(article).find("#registeredUserDesc").html json.description
  $("#buzz").find("article:last").after article
showNewProposal = (json) ->
  article = $("#buzz").find("article:first").remove()
  $(article).find("#registeredUserPictureUrl").attr "src", json.pictureUrl
  $(article).find("#registerdUserName").html json.speakerName
  $(article).find("#registerdUserTwitterId").html json.twitterId
  $(article).find("#registeredUserDesc").html "New Talk: " + json.title
  $("#buzz").find("article:last").after article
showProposal = (json) ->
  $("#speakerImage").attr "src", json.pictureUrl
  $("#speakerName").html json.name
  $("#twitterId").attr "href", "https://twitter.com/" + json.twitterId
  $("#twitterId").html json.twitterId
  $("#title").html json.title
  $("#proposal").html json.proposal
$(document).ready ->
  websocket = new WebSocket $("#ws-url").val()
  websocket.onmessage = (evt) ->
    json = JSON.parse(evt.data)
    showProposal json  if json.messageType is "proposalSubmission"
    showRegisteredUser json  if json.messageType is "registeredUser"
    showNewProposal json  if json.messageType is "newProposal"

  websocket.onopen = ->
    jsRoutes.controllers.Application.recentUsers(4).ajax success: (data) ->
      $("#buzzHeader").css "color", "#7CB232"


  websocket.onerror = (evt) ->
    alert "error " + evt
