# cert_gen
Service for certificates generation
Function generating_file accepts a POST request and user data in JSON format, and generates a PDF certificate based on the provided data. The date is automatically set at the time of creation.
All certificates are saved as PDF files locally. Additionally, certificate models are created in the database.
For clarity and testing purposes, a simple template for entering information has been created (not styled).

# certificate upload into google drive defined folder
1. you have to create Google Api Service Account. then download json file
 and put it into root folder of project with it's name.
 Example: certificate-generation-name.json
 You have to put it on gitignore immediately
 Its excluded from mutual repository by gitignore, every participant has to create
 his own file.
2. I added empty example file -you gonna put here your json and rename it,
 but you should post here your real own data from Google Api Service Account.