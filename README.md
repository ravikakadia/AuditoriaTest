# AuditoriaTest

To send email using Gmail API perform below steps:
1. if user does not have any project on Google Cloud, create a new project on Google Cloud using - https://console.developers.google.com/cloud-resource-manager
2. enable Gmail API for above project using - https://console.developers.google.com/apis/library
3. Prepare OAuth clinet - https://console.developers.google.com/apis/credentials
4. Download the client json file and save it as credentials.json under resources directory
5. In the GmailService.java class update toEmailAddresss and fromEmailAddress as per current user's crdentials.

Note: Keep SampleFile.rtf in resources directory.
