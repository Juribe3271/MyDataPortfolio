San Francisco Library Usage Dashboard
This project provides a comprehensive data analysis and visualization solution for understanding the usage patterns of the San Francisco Public Library system. It leverages a Java backend for data processing and API exposure, and a React frontend for interactive data visualization.

Table of Contents
Project Overview

Features

Technologies Used

Key Findings

Setup and Running the Project

Data Source

Future Enhancements

Contact

Project Overview
The primary goal of this project is to transform raw library usage data into actionable insights, demonstrating proficiency in full-stack data application development. It covers the pipeline from data loading into a relational database to serving that data via a custom API and visualizing it interactively in a web dashboard.

Features
Data Loading & Storage: Efficiently loads large CSV datasets into a MySQL relational database, ensuring data persistence and structured access.

Java Backend API: Develops a lightweight Java HTTP server using com.sun.net.httpserver and Gson to expose RESTful API endpoints, providing structured JSON data to the frontend.

React Frontend Dashboard: Builds a responsive and interactive web dashboard with React.js, styled using Tailwind CSS, and featuring dynamic charts powered by Recharts.

Real-time Data Visualization: The dashboard dynamically fetches and displays data from the running Java backend API, ensuring visualizations reflect the most current database information.

Technologies Used
Backend (Java):

Java 11+

Maven (Project Management)

MySQL (Database)

JDBC (Database Connectivity)

Apache Commons CSV (CSV Parsing)

Gson (JSON Serialization/Deserialization)

com.sun.net.httpserver (Simple HTTP Server)

Frontend (React):

React.js

Create React App (Development Environment)

Tailwind CSS (Styling)

Recharts (Charting Library)

Lucide React (Icons)

HTML5, CSS3, JavaScript (ES6+)

Version Control:

Git & GitHub

Key Findings
Based on the visualized library usage data, here are some key insights:

Dominance of Adult Patron Type: The "ADULT" patron type consistently accounts for the highest volume of both total checkouts and total renewals, significantly outperforming other categories. This highlights adult users as the primary demographic engaging with library resources.

High Engagement Among Specific Age Ranges: Age ranges such as "10 to 19 years", "45 to 54 years", and "65 to 74 years" show the highest average checkouts. This suggests these demographics are particularly active library users, potentially for educational, professional development, or leisure reading.

Seasonal Usage Peaks: The data reveals clear seasonal patterns in library usage. There is a noticeable surge in activity (both checkouts and renewals) during specific months (e.g., May and July), indicating increased library engagement during certain periods of the year, possibly linked to school holidays, summer reading programs, or other community initiatives.

Checkout vs. Renewal Ratios: While checkouts generally exceed renewals across all patron types and months, the specific ratio between them varies. Analyzing this ratio for different segments can offer deeper insights into user behavior and preferences (e.g., whether users prefer to keep items longer or frequently borrow new ones).

Setup and Running the Project
To get the San Francisco Library Usage Dashboard running on your local machine, follow these steps:

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
