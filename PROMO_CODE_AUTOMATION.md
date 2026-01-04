# 🚀 Deployment Guide: Standalone Promo Automation

Use this version to bypass the Google "Deleted Client" error.

## Step 1: Create the Project
1. Go to **[script.google.com](https://script.google.com)**.
2. Click **+ New Project**.
3. **Delete** everything in the editor (delete `myFunction`).

## Step 2: Paste this Code
Copy and paste this exact block. **Make sure to put your Google Sheet URL in the first line.**

```javascript
// 1. YOUR NEW SHEET URL
const SHEET_URL = "https://docs.google.com/spreadsheets/d/1Cmy_BeYgfI6Imk923OMZRUMCOZ6nycDt93KOMV1Oats/edit?resourcekey=&gid=1909983230#gid=1909983230"; 

// 2. CONFIGURATION
const SHEET_NAME_CODES = "Codes"; 
const EMAIL_SUBJECT = "Your Sound Timer Promo Code 🎁";
const EMAIL_BODY_TEMPLATE = "Hi there,\n\nThanks for helping test Sound Timer! Here is your promo code to download the app for free:\n\n{{CODE}}\n\nInstructions:\n1. Open Google Play Store\n2. Tap your profile icon > Payments & subscriptions > Redeem code\n3. Enter the code above.\n\nThanks,\nSound Timer Team";

function onFormSubmit(e) {
  // Opening by URL is more robust
  const sheet = SpreadsheetApp.openByUrl(SHEET_URL);
  
  let email = "";
  if (e && e.namedValues) {
    email = e.namedValues["Email Address"] ? e.namedValues["Email Address"][0] : "";
    if (!email && e.namedValues["email"]) email = e.namedValues["email"][0];
  } 
  
  if (!email) {
    const responseSheet = sheet.getSheetByName("Form Responses 1"); 
    const lastRow = responseSheet.getLastRow();
    email = responseSheet.getRange(lastRow, 2).getValue(); 
  }

  email = email.trim();
  if (!email || !email.includes("@")) return;

  const code = getAndMarkCode(sheet);
  if (!code) {
    MailApp.sendEmail(Session.getActiveUser().getEmail(), "Sound Timer: OUT OF CODES", "No more promo codes available!");
    return;
  }

  const body = EMAIL_BODY_TEMPLATE.replace("{{CODE}}", code);
  MailApp.sendEmail(email, EMAIL_SUBJECT, body);
}

function getAndMarkCode(sheet) {
  const codeSheet = sheet.getSheetByName(SHEET_NAME_CODES);
  const lastRow = codeSheet.getLastRow();
  if (lastRow < 1) return null;
  const data = codeSheet.getRange(1, 1, lastRow, 2).getValues();
  
  for (let i = 0; i < data.length; i++) {
    if (data[i][0] != "" && data[i][1] !== "USED") {
      codeSheet.getRange(i + 1, 2).setValue("USED");
      return data[i][0];
    }
  }
  return null;
}
```

## Step 3: Save & Trigger
1. Press **Ctrl + S** and name it `PromoSenderMaster`.
2. Click the **Clock Icon** (Triggers) on the left.
3. Click **+ Add Trigger**.
4. Settings:
   - **Function to run**: `onFormSubmit`
   - **Event source**: `From spreadsheet`
   - **Event type**: `On form submit`
5. Click **Save** and **Allow** permissions (Advanced -> Go to PromoSender -> Allow).

