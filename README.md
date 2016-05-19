How do I get set up?
Mac OSX build guide with Eclipse IDE
You will need to have Eclipse with Java JDK8 and nodeJS installed.
Then you can start to deploy this project on your local. First go to the Clone page and get your own url for cloning this project and run
git clone https://yourusername@bitbucket.org/tariquesiddiqui/zenvisage.git
The next step would be using Eclipse to load and build this project. You can open up Eclipse and go to File->Import->Projects from Git. Once the project is imported in Eclipse, you can right click on the project and go to team->switch to choose the latest branch you want to work with.
If you have successfully built the project in Eclipse, you can now install the required dependencies for nodeJS.
cd ~
cd zenvisage
npm install
These commands will help you install the dependencies assuming your local repository is named zenvisage. Note that this process may have errors. Please contact us if you have problems with installing dependencies.
node app.js
Now you can launch the project and see the User Interface in localhost:8999 in your browser!