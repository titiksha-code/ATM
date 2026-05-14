# 🚀 How to Upload This Project to GitHub

## Step 1 — Create GitHub Repository
1. Go to https://github.com
2. Click green **New** button
3. Repository name: `GlobalBank-ATM-Java`
4. Description: `Console-based ATM System — Java + MySQL | B.Tech CSE Project`
5. Set to **Public**
6. Do NOT check "Add README" (we already have one)
7. Click **Create Repository**

---

## Step 2 — Open Terminal in VS Code
Press **Ctrl + `** (backtick) inside VS Code

---

## Step 3 — Run These Commands One by One

```bash
# 1. Go into your project folder
cd path/to/ATM_Project

# 2. Initialize git
git init

# 3. Add all files
git add .

# 4. First commit
git commit -m "🏦 Initial commit - GlobalBank ATM System (Java + MySQL)"

# 5. Rename branch to main
git branch -M main

# 6. Connect to your GitHub repo (replace YourUsername)
git remote add origin https://github.com/YourUsername/GlobalBank-ATM-Java.git

# 7. Push to GitHub
git push -u origin main
```

---

## Step 4 — Done! ✅
Visit `https://github.com/YourUsername/GlobalBank-ATM-Java`
Your project is live with the full README displayed!

---

## Future Updates
Whenever you make changes to the code:
```bash
git add .
git commit -m "describe your change here"
git push
```
