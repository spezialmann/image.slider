# image.slider
spring boot app "image slider" from dropbox folder 

## Features
 - Synchronise images from a specific dropbox folder 
 - persist images on your server file system
 - save the image meta data in a h2 database
 - Scheduled synchronization via dropbox api

## What you need
- An Dropbox account [www.dropbox.com](http://www.dropbox.com)
- [optional] email to dropbox service e. g. [https://sendtodropbox.com/](https://sendtodropbox.com/) (free)

## Technology stack
- Spring Boot
- Thymeleaf
- H2 database
  
##Configuration
@ses application.properties

    server.port=8085
    spring.thymeleaf.mode=LEGACYHTML5
    #Dropbox config
    dropbox.access.token=<YOUR ACCESS TOKEN>
    dropbox.image.directory=/Apps/Attachments
    dropbox.client.id=dropbox/image.slider
    dropbox.client.user.locale=de_DE
	#Local config
    locale.image.directory=/tmp/images