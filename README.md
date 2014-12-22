¿que es?
========

Que es is an app that you can take a picture to learn what an object is called in other languages.

There are many ways that one can learn a new language. "Que es?" which means "What is it?" in spanish, takes a different approach where a user can take a picture of an object to learn what the object is called in other languages. Some would argue that it is not the most practical way to learn a new language, but it's a fun and intuitive way of learning a new word in other language, especially for children who are less comfortable with dictionaries.

In the field of computer science, computer vision is one area that has many on-going researches in almost every institute, and it is a very hard problem to identify objects portrayed in a 2D image. "Que es," therefore, will begin with the most humble goal of identifying a few objects that are easily distinguishible and hopefully develop into a better AI system, with the help of its users.

I hope the users of this app would find it somewhat useful, but most importantly to have fun and be amazed at what their android phone is capable of.

How it works
------------

Please refer to the following mock-ups for the design of the basic UI for the application.

![alt tag](<https://cloud.githubusercontent.com/assets/8993322/4572053/09c404fe-4f81-11e4-8aa2-d6ea216fa3ad.png>)

Notes
-----

While the app itself is simple, the technology behind object identification will not be trivial. Also as pointed out by many researches on UI/UX, shortening the amount of time for analyzing the image will also be another difficulty. The initial plan is to utilize OpenCV with its edge-detection and color intensity determination capabilities to identify an object, and then use some basic machine-learning algorithms to remember the pattern. If this is proved to be non-trivial or too difficult to finish in time, plan B is to utilize Google Image Search platform, and return a result based on the metadata of the returned images.
