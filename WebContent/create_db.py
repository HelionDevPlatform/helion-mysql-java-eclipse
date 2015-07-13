'''
Created on June 10, 2015

@author: jacoba
'''

import urlparse
import os
import logging
import json

# create simple table
if __name__ == '__main__':
    
    database_name = 'helion-mysql-java-db'
    try:
        logging.basicConfig()
        log = logging.getLogger('mysql_java')
        log.setLevel(logging.DEBUG)

        log.debug("getting  database connection info from VCAP_SERVICES environment variable")
        mysqlUrl = ''
        
        try:
            found = False
            vcap_str = os.environ['VCAP_SERVICES']
            vcap_svcs = json.loads(vcap_str)
            mysql_objs = vcap_svcs['mysql'];
            
            for db in mysql_objs:
                if db['name'] == database_name:
                    found = True
                    mysqlUrl = urlparse.urlparse(db['credentials']['uri'])
                    break
                    
            
            if found == False:
                log.error("unable to find database associated with this application: %s"%database_name)
                exit(1)
                    
        except KeyError:
            log.error("environment variable VCAP_SERVICES not found, cannot continue")
            exit(1)
        
        
        url = mysqlUrl.hostname
        log.debug("url = %s"%url)
        password = mysqlUrl.password
        log.debug("password = %s"%password) 
        userName = mysqlUrl.username
        log.debug("username = %s"%userName)
        dbName = mysqlUrl.path[1:] # slice off the '/'
        log.debug("dbName = %s"%dbName) 
        ## NOTE the database is already created for you. 
        ## just need to create the table below
        
        # hack to run local version
        
        try:
            mysqlPath = os.environ['FQ_MYSQL']
        except Exception as e:
            log.warning("FQ_MYSQL not found, defaulting to mysql")
            mysqlPath = 'mysql'
            
        createString = '%s  -h %s -D %s -u %s -p%s -e"use %s; CREATE TABLE IF NOT EXISTS text_data( text_id int not null auto_increment, contents char(100) not null,PRIMARY KEY(text_id));"'%(mysqlPath,url,dbName,userName,password,dbName)
        log.debug("about to run: %s "%createString)
        os.system(createString)
        
       
    except Exception as e:
        print "Exception during database initialization: %s"%str(e)
