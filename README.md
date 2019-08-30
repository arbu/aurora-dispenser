# Aurora Token Dispenser

Stores anonymous email-password pairs, gives out Google Play Store tokens.

# Building
* `git clone https://github.com/whyorean/aurora-dispenser`
* `cd aurora-dispenser `
* Edit src/main/com/aurora/tokenizer/Tokenizer.java
* Put your email and password pair in authMap
* `./gradlew jar`
* `java -jar target/token-dispenser.jar`

# Usage

You can get the tokens for regular requests from 
`http://server-address:port/token/email/youremail@gmail.com`

You can get the tokens for checkin requests at 
`http://server-address:port/token-ac2dm/email/youremail@gmail.com`


Default port used is 8080, you can use any as per your convenience

# Credits
* https://github.com/yeriomin/token-dispenser
