# Introduction
This project contains sample code to implement IF2 of the Intercor project. The interface specifications can be found in [on the project website](http://intercor.diviprojects.wpengine.com/wp-content/uploads/sites/15/2018/03/InterCor_M4-Upgraded-Specifications-Hybrid_v1.0.pdf)

The code is by no means robust, and should not be used in an operational environment. It is merely intended to demonstrate the basic functions that are required to implement the Intercor IF2 interface. 

 The default connection parameters are coded in [IF2Client.java](src/main/java/intercor/if2/client/IF2Client.java), but can be modified by providing them on the 
command line, e.g. username=user password=pwd etc. See [IF2Client.java](src/main/java/intercor/if2/client/IF2Client.java) for all
available parameters.
