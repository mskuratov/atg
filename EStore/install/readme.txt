When running on Linux/Solaris Configurations, ensure the OS user has execute rights on .sh files before executing them. Following command may be used to change the file access rights- 

e.g.
chmod 755 <fileName> : Gives all rights to the owner, and only read and execute to group and others (rwxr-xr-x)

The sh files which may require permissions to be edited are as follows:
1. <db_name>/createStoreSchema.sh
