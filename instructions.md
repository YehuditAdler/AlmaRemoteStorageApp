# Instructions
## Setup
Before installation please make sure you have a FTP server

### On all Institutions:
1. FTP connection - To share files between app and alma institutions when [sub-directory](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/050Administration/050Configuring_General_Alma_Functions/050External_Systems#UpdateSubmissionFormatFtp) would be the main folder for all institutions.
2. Publishing Profile - To handle items synchronization
    create a [set](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/050Administration/070Managing_Jobs/060Managing_Search_Queries_and_Sets#sets.setDetail) how contains all items that are located in the remote storage institution.
    create a [publish profile job](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/090Integrations_with_External_Systems/030Resource_Management/080Publishing_and_Inventory_Enrichment) item level with above set. publishing protocol should be FTP and sub-directory is the "ins_code"/items for example : 01AAA_ABC/items. Compressed file extension : tar.gz
3. Remote Storage Facility - To export all requests to remote storage
    create a [integration profile](https://developers.exlibrisgroup.com/alma/integrations/remote_storage/xml_based/)  of “remote storage” type. when “Export File Path“ is "ins_code"/requests for example : 01AAA_ABC/requests.
    create a [Remote Storage](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/030Fulfillment/080Configuring_Fulfillment/040Configuring_Remote_Storage_Facilities) conected to your integration profile.
    edit [Physical Location](https://knowledge.exlibrisgroup.com/Alma/Product_Documentation/010Alma_Online_Help_(English)/030Fulfillment/080Configuring_Fulfillment/030Configuring_Physical_Locations) - Type is : Remote Storage , Remote Storage is the remote storage facility you created.
    
## Installation

1. Clone this repository: `git clone https://github.com/jweisman/alma-blacklight.git`
2. Install dependencies: `bundle install`
3. Copy the `application.example.yml` file to `application.yml` and replace the placeholder values.
4. Run the rake task to populate the index: `rake oai_harvest`
5. Run the application: `bin\rails server` for WEBrick 
