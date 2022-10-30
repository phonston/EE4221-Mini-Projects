## Mini-projects

Code used to automatically setup on amazon EC2 instance

Install openjdk 19
```
sudo yum install java java-devel
```

Clone this repository
```
git clone https://github.com/phonston/EE4221-Mini-Projects.git
```

Compile the classes first
```
javac -d EE4221-Mini-Projects/hw1/bin EE4221-Mini-Projects/hw1/src/student/*
```

Then run the program
```
java --enable-preview -cp EE4221-Mini-Projects/hw1/bin student.ServerDDDDDDDD
or
java --enable-preview -cp EE4221-Mini-Projects/hw1/bin student.ClientDDDDDDDD
```