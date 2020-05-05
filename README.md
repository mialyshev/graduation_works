## Instruction

1. Clone this repository  
2. Clone the repository of the project whose tickets you want to analyze  
3. Create a jar file using the mvn package command from the downloaded repository  
4. Create a _config.properties_ file and fill it as follows:  
    ```
    url=https://github.com/mialyshev/webservice  
    path=/home/mialyshev/guava  
    projectname=guava  
    issuespath=scan.txt  
   ```
    
    * url - Link to github project whose tickets you want to analyze  
    * path - Path to the repository cloned in step 2. If the project is in the same directory as the jar file obtained in step 3, then you can specify only the name of the folder, not the full path    
    * projectname - Name of the project cloned in step 2  
    * issuepath - The path to the file where information about tickets that have already been analyzed will be stored. If the file is in the same directory as the jar file obtained in step 3, then you can specify only the name of the file, not the full path  
5. Run the blamer.jar file using the following keys:  
    * -c,--config properties <arg>   Path to file 'config.properties'.    
    * -f,--file <arg>                The name of the file to display in html. (_Default = output.html_)  
    * -t,--token <arg>               Personal access token from your github for authorization **(Required)**   
        1. Go to your github account settings  
        2. After that, go to the tab "Developer settings"  
        3. After go to the tab "Personal access tokens"  
        4. Click to the button "Generate new token"  
        5. From the proposed list of options, select "repo", "user" and after that click the "Generate token" button  
        6. After that, you will receive your token  
    * -o,--out <arg>                 You can write either 'screen' or 'html'. (_Default = screen_)      
    * -s,--start <arg>               Ticket start number. (_Default = 0_)  
    * -e,--end <arg>                 Ticket end number. (_Default = 100000_)    
    * -m,--mode <arg>                Program mode. (_Default = 0_):  
        * mode "report" - out all information to screen or html page  
        * mode "autoassignee" - no screen output. "Autoassignee mode." The mode when the program itself assigns assignee to the desired ticket
        * mode "htmlreport" - out to html page. The "Assignee" button appears with each ticket that makes an assignee  
