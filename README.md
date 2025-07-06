San Francisco Library Usage Dashboard
This repository contains the code for a data analysis project focused on understanding the usage patterns of the San Francisco Public Library system. It features a Java backend for data processing and API exposure, and a React frontend for interactive data visualization.

The primary goal of this project is to provide insights into library usage, including checkouts and renewals by patron type, age range, and monthly trends.

Features
Data Loading: Loads library usage data from a CSV file into a MySQL database.

Java Backend API: A simple Java HTTP server (using com.sun.net.httpserver) that exposes RESTful API endpoints to query processed library data.

React Frontend Dashboard: An interactive web dashboard built with React.js and Tailwind CSS, featuring charts powered by Recharts to visualize key metrics.

Real-time Data: The frontend fetches data directly from the running Java backend API, ensuring the visualizations reflect the current state of the database.

Technologies Used
Backend (Java)
Java 11+

Maven: For project management and dependency handling.

MySQL: Relational database for storing library usage data.

JDBC: Java Database Connectivity for interacting with MySQL.

Apache Commons CSV: For robust CSV file parsing.

Gson: Google's JSON library for converting Java objects to JSON responses for the API.

com.sun.net.httpserver: Built-in Java HTTP server for creating API endpoints.

Frontend (React)
React.js: JavaScript library for building user interfaces.

Create React App: For setting up the React development environment.

Tailwind CSS: A utility-first CSS framework for styling.

Recharts: A composable charting library built on React components.

Lucide React: A collection of open-source icons.

HTML5, CSS3, JavaScript (ES6+)

Key Findings
Based on the visualized data, here are some key insights into San Francisco Library usage:

Dominance of Adult Patron Type: The "ADULT" patron type consistently shows the highest numbers for both total checkouts and total renewals, significantly surpassing other categories. This indicates that adult users are the primary consumers of library resources.

High Engagement Among Specific Age Ranges: Age ranges such as "10 to 19 years", "45 to 54 years", and "65 to 74 years" exhibit the highest average checkouts. This suggests these demographics are particularly active library users, possibly for educational, professional, or leisure reading purposes.

Seasonal Usage Peaks: Library usage, particularly total checkouts and renewals, demonstrates clear seasonal patterns. There is a notable surge in activity during certain months (e.g., May and July as observed in the screenshots), suggesting increased library engagement during specific periods of the year, possibly linked to school holidays, summer reading programs, or other community events.

Renewal vs. Checkout Ratios: While checkouts are generally higher than renewals across all patron types and months, the ratio between them can vary. Analyzing this ratio for different segments can provide insights into user behavior (e.g., how often users prefer to keep items longer versus returning and borrowing new ones).

Setup and Running the Project
Follow these steps to get the San Francisco Library Usage Dashboard running on your local machine.

Prerequisites
Java Development Kit (JDK) 11 or higher: Download & Install JDK

Maven: Download & Install Maven

Node.js (LTS version) and npm: Download & Install Node.js

MySQL Server: [suspicious link removed]

MySQL Workbench or a similar client: For managing your MySQL database.

Git: Download & Install Git

Backend Setup (Java & MySQL)
Clone the Repository:

git clone https://github.com/Juribe3271/MyDataPortfolio.git
cd MyDataPortfolio/SF_Library_Analysis/backend/sf-library-app

Set up MySQL Database:

Open MySQL Workbench (or your preferred MySQL client).

Create a new database (e.g., sflibrary_db).

Create a user with appropriate permissions for this database (e.g., sflibrary_user with password password).

Note: Ensure your MySQL server is running.

Update Database Configuration:

Open the src/main/java/com/sflibrary/app/DatabaseConfig.java file.

Update the DB_URL, DB_USER, and DB_PASSWORD constants to match your MySQL setup.

public static final String DB_URL = "jdbc:mysql://localhost:3306/sflibrary_db?useSSL=false&serverTimezone=UTC";
public static final String DB_USER = "sflibrary_user";
public static final String DB_PASSWORD = "password";

Place the CSV Data File:

Ensure the Library_Usage.csv file is located in the src/main/resources/ directory of your sf-library-app project. If it's not there, download it and place it in this directory.

Build and Run the Java Backend:

Navigate to the sf-library-app directory in your terminal:

cd C:\Users\joseu\Documents\MyDataPortfolio\SF_Library_Analysis\backend\sf-library-app

Build the project using Maven:

mvn clean install

Run the Main class to load data and start the API server:

mvn exec:java -Dexec.mainClass="com.sflibrary.app.Main"

You should see console output indicating that the data is being loaded and the "API Server started on port 8080". Keep this terminal window open as long as you want the API server to run.

Frontend Setup (React)
Navigate to the Frontend Directory:

Open a new terminal window.

Navigate to the frontend project directory:

cd C:\Users\joseu\Documents\MyDataPortfolio\SF_Library_Analysis\frontend\sf-library-dashboard

Install Node.js Dependencies:

npm install

This will install all required React libraries, including recharts and lucide-react.

Start the React Development Server:

npm start

This will compile the React application and open it in your default web browser (usually at http://localhost:3000).

View the Dashboard:

Ensure your Java backend API server is running (from the previous steps).

Your React dashboard in the browser should now display the San Francisco Library Usage charts populated with data fetched directly from your MySQL database via the Java API.

Data Source
The data used in this project is sourced from the San Francisco Public Library's publicly available usage records.

Future Enhancements
Implement user authentication and authorization.

Add more interactive filters and drill-down capabilities to the dashboard.

Integrate a more robust Java web framework (e.g., Spring Boot) for the backend.

Deploy the application to a cloud platform (e.g., Google Cloud, AWS, Azure).

Add unit and integration tests for both frontend and backend.
