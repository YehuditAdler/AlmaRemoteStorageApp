# Instructions
## Prerequisites
FTP server - with sub-directories for each institution.

Access to the Developer network for all member institutions.

### On all Institutions:
1. FTP connection configuration - To share files between the App and Alma with a  [sub-directory](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/050Administration/050Configuring_General_Alma_Functions/050External_Systems#UpdateSubmissionFormatFtp) for each institutions (the dir name should include the inst code. e.g. main_folder/01AAA_ABC).
2. API-key with r/w permission for the Bibs area
3. Publishing Profile - To handle items synchronization
    - Create a [set](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/050Administration/070Managing_Jobs/060Managing_Search_Queries_and_Sets#sets.setDetail) which contains all items that are located in the remote storage location.
    - Create an Items [publishing profile job](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/090Integrations_with_External_Systems/030Resource_Management/080Publishing_and_Inventory_Enrichment) with the above set. Publishing protocol should be FTP and sub-directory is "items". Compressed file extension: tar.gz
4. Remote Storage Facility - To export all requests to remote storage
    - Create a [integration profile](https://developers.exlibrisgroup.com/alma/integrations/remote_storage/xml_based/)  of “remote storage” type. “Export File Path“ is "requests".
    - Create a [Remote Storage](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/030Fulfillment/080Configuring_Fulfillment/040Configuring_Remote_Storage_Facilities) conected to your integration profile.
    - Edit [Physical Location](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/030Fulfillment/080Configuring_Fulfillment/030Configuring_Physical_Locations) - Type is : Remote Storage , Remote Storage is the remote storage facility you created.
    - Find the job ID that should be used for submitting the job and add it to the app configuration. You can use this API: /almaws/v1/conf/jobs?type=SCHEDULED&limit=100 search for the integration profile name and get the id.
5.  [Webhooks](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/090Integrations_with_External_Systems/030Resource_Management/300Webhooks)
    - Create a Webhooks Integration Profile. Message type is JSON and Under Subscriptions Select `Job Finish` to send a webhook when a Job is finished. Webhook listener URL will be the url after deploying the app with a following Forward Slash and webhook: $url/webhook.
### On Remote Storage Institution:
1. Create patrons for each Institution_Lirary or example if Institution code is 01AAA_ABC and libraries code is RS the users Primary identifier will be 01AAA_ABC_RS
2. Create [provenance code](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/040Resource_Management/080Configuring_Resource_Management/080Configuring_Provenance_Codes) for each institution code.
3. add personal delivery for items [terms of use](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/030Fulfillment/080Configuring_Fulfillment/050Physical_Fulfillment#fulfillment.tou.termsOfUseManagement)
4. Create a [Webhooks](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/090Integrations_with_External_Systems/030Resource_Management/300Webhooks) Integration Profile. Message type is JSON and Under Subscriptions Select Loans to send a webhook when a loan is returned. Webhook listener URL will be the url after deploying the app with a following Forward Slash and webhook: $url/webhook.



## Installation

1. Install [git](https://git-scm.com/downloads).
2. Install [Heroku](https://devcenter.heroku.com/articles/getting-started-with-java#set-up).
3. Clone this repository: `git clone https://github.com/YehuditAdler/AlmaRemoteStorageApp.git`
4. Go to repository folder `cd AlmaRemoteStorageApp`
5. Remove .git folder
6. Move the `conf.json` file to FTP under main-folder and replace the institutions values :gateway url , api keys, ftp server detailed , requests job id....
7. Commit to Git: `git init` , `git add .` , `git commit -m "Ready to deploy"`
8. Create the heroku app `heroku create “app-name“`
9. Add conf.json path to the [Config Vars](https://devcenter.heroku.com/articles/config-vars#using-the-heroku-dashboard) when Key=CONFIG_FILE and Value=ftp://user:password@server/path/to/conf.json
9. Deploy your code `git push heroku master`
10. The application is now deployed. Ensure that at least one instance of the app is running: `heroku ps:scale web=1`
11. Congratulations! Your web app should now be up and running on Heroku. Open it in your browser with: `heroku open`
12. Now you have the remote url in browser - add it to Webhook listener URL

### WAKE UP HEROKU
Free dynos are unique because they go to sleep after 30 minutes of inactivity.
What we can do is run a Bash Shell Script on Windows to Prevent Your Heroku App From Sleeping:

- RepeatPing.bat file :
```
@echo OFF
:REPEAT
@echo. %date% at %time% >>PingLogs.txt
curl  “heroku remote url“
timeout /t 1800 /nobreak > NUL
goto REPEAT
```

