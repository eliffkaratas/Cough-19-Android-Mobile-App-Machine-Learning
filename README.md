# Cough-19
Android Application development in Java for pre-diagnosis of Covid-19 with using Machine Learning algorithms in Python

The Cough-19 Android mobile application, developed in our MOBILE APPLICATION TO DIAGNOSE THE DISEASE project, is an application that aims to reduce the effects of the pandemic, improve public and individual health, and shed light on the questions in people's minds without the need to go to the hospital. At the same time, it has the feature of being a decision support system for doctors in hospitals. Thanks to the Cough-19 application, where we can record the voice of the user, it aims to improve the studies on the pandemic by collecting voice data and to be useful to the user by calculating the risk of the user to become COVID-19 through the application.

Cough-19 was developed in Java language in Android Studio.

User data is stored in Firebase's Authentication, Firestore Database, Realtime Database and Storage applications.

The processing of audio and Machine Learning algorithms are carried out with Python language in Google Colab. The MFCC values of each audio data are calculated in Python, Google Colab.

Since Cough-19 is an Android-based mobile application, it can be easily used by anyone. Thanks to its easy interface, it is understandable by most people. 

The user becomes a member of the application with own e-mail address. 
In this way, each user's data is stored under a unique ID. 
After becoming a member, the user's name, surname, blood group, chronic diseases and similar information are requested. 
It is saved in the user profile. 
The user who created his profile can add an image of himself/herself, if the user wishes, the user can record the cough sound and view the COVID-19 risk analysis. 
If the user wishes, the user can help us improve the application by creating feedback and giving positive/negative feedback about the application.

Models used in machine learning are Artificial Neural Networks, Support Vector Machine and Decision Tree. The created models are trained with the MFCC values of the audio data.

The metric we calculate is accuracy.

Among the models we trained, the highest accuracy rate with 90% and above results belongs to the Artificial Neural Networks model.

The value we show as a result on the Cough-19 application is the accuracy value of the Artificial Neural Networks model.

The highest accuracy value that Neural Network, Support Vector Machine, and Neural Network applied is get from Neural Network model.

Neural Network -> 96.49%
Decision Tree -> 88.24%
Support Vector Machine -> 94.12%

We enter the audio data of the user we get from Firebase as input to the model we trained with Neural Network. Therefore, the value we show to the user in the mobile application is the accuracy value we obtained from the Neural Network model.
