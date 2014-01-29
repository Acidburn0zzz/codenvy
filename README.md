CODENVY CLI
===

- Website: http://docs.codenvy.com/sdk
- Source: http://github.com/codenvy/cli
- Mailing List: [Google Groups] (https://groups.google.com/a/codenvy.com/forum/#!forum/helpdesk)

Codenvy makes SaaS developer environments.  We provide those environments in our public cloud hosted at codenvy.com, or through on premises installations with Codenty Enterprise.  This CLI tool provides command line access to Codenvy installations.  This CLI is portable across MacOS, Linux, and Windows.

Quick Start
-----------
You will need to have [Java version 6] (http://java.com/en/download/index.jsp) or later installed on your system.  Download the CLI library and unzip it in a directory.  Once downloaded, you will need to update the `JAVA_LOCATION` variable in the startup script to point to the root directory where Java is installed on your computer.  On MacOS or Linux this is the `codenvy.sh` file.  For windows, this is the `codenvy.bat` file.

    JAVA_LOCATION=path_to_your_Java_installation
    
Test your configuration by entering `codenvy` in the console window.  You should see the CLI help message appear.

Getting Started Guide
---------------------
To learn how to interact with remote Codenvy systems, create new projects, load them into the system, and analyze their behavior, view the [Getting Staryed Guide.] (http://docs.codenvy.com/cli)

Contributing to the CLI
-----------------------
To hack on the CLI (or Codenvy) you will need [maven] (http://maven.apache.org/) installed.  Once maven is installed, you will need to have maven gain access to the Codenvy central nexus server to download libraries.  Add the following configuration item to your `settings.xml` file either stored in `M2_HOME` or `M2_REPO`.

     <mirrors>
       <mirror>
         <id>repo.codenvy.private</id>
         <mirrorOf>external:*</mirrorOf>
         <url>https://maven.codenvycorp.com/content/groups/codenvy-private-group/</url>
       </mirror>
     </mirrors>
    
Once your maven is properly configured, you can build the CLI by entering

    mvn clean install

