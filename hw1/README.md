## Mini-projects

Code used to automatically setup on amazon EC2 instance

Install openjdk 19
```
sudo yum install java
```

Compile the classes first
```
javac -d bin src/student/*
```

Then run the program
```
java --enable-preview -cp /home/phonston/Documents/EE4221/Assignment_1/hw1/bin student.ServerDDDDDDDD
or
java --enable-preview -cp /home/phonston/Documents/EE4221/Assignment_1/hw1/bin student.ClientDDDDDDDD
```