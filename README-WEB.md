# 🌐 AI-Powered Java Code Reviewer - Web Application

A full-stack web application with separate frontend and backend servers for AI-powered Java code review.

## 🏗️ Architecture

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│   Frontend      │ ←─────────────→ │   Backend       │
│   (React)       │                 │   (Spring Boot) │
│   Port: 3000    │                 │   Port: 8080    │
│                 │                 │                 │
│ • Web UI        │                 │ • REST APIs     │
│ • File Upload   │                 │ • AI Integration│
│ • Results View  │                 │ • Database      │
└─────────────────┘                 └─────────────────┘
```

## 🚀 Quick Start

### **Option 1: Start Both Servers (Recommended)**
```bash
node start-both.cjs
```

### **Option 2: Start Servers Separately**

**Terminal 1 - Backend:**
```bash
start-backend.bat
```

**Terminal 2 - Frontend:**
```bash
start-frontend.bat
```

## 📋 Prerequisites

- **Java 17+** (for backend)
- **Node.js 16+** (for frontend)
- **Maven** (or use Maven wrapper)
- **API Keys** for AI providers

## 🔧 Setup Instructions

### 1. **Backend Setup (Spring Boot)**
```bash
cd backend
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

**Backend will run on:** http://localhost:8080

### 2. **Frontend Setup (React)**
```bash
cd frontend
npm install
npm start
```

**Frontend will run on:** http://localhost:3000

### 3. **API Keys Configuration**

Set environment variables or update `backend/src/main/resources/application.properties`:

```properties
app.ai.openai.api-key=your-openai-key
app.ai.huggingface.api-key=your-huggingface-key
app.ai.claude.api-key=your-claude-key
```

## 🌐 Access Points

- **Frontend Application:** http://localhost:3000
- **Backend API:** http://localhost:8080/api
- **H2 Database Console:** http://localhost:8080/h2-console
- **API Documentation:** http://localhost:8080/swagger-ui.html

## 📁 Project Structure

```
java.project/
├── backend/                          # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/javacodereviewer/backend/
│   │       ├── BackendApplication.java
│   │       ├── controller/           # REST Controllers
│   │       ├── service/              # Business Logic
│   │       ├── repository/           # Data Access
│   │       ├── entity/               # JPA Entities
│   │       └── model/                # DTOs
│   ├── src/main/resources/
│   │   └── application.properties    # Configuration
│   └── pom.xml                       # Maven Dependencies
├── frontend/                         # React Frontend
│   ├── src/
│   │   ├── components/               # React Components
│   │   ├── services/                 # API Services
│   │   └── App.js                    # Main App
│   ├── public/
│   └── package.json                  # NPM Dependencies
├── start-both.cjs                    # Start both servers
├── start-backend.bat                 # Start backend only
├── start-frontend.bat                # Start frontend only
└── README-WEB.md                     # This file
```

## 🔌 API Endpoints

### **Code Review**
- `POST /api/reviews/review` - Review Java code
- `GET /api/reviews` - Get all reviews
- `GET /api/reviews/{id}` - Get review by ID
- `GET /api/reviews/recent` - Get recent reviews
- `DELETE /api/reviews/{id}` - Delete review

### **Search & Filter**
- `GET /api/reviews/search?keyword=...` - Search reviews
- `GET /api/reviews/provider/{provider}` - Filter by AI provider

### **Utilities**
- `GET /api/reviews/providers` - Get available AI providers
- `GET /api/reviews/stats` - Get statistics

## 🎨 Frontend Features

- **Modern React UI** with Tailwind CSS
- **File Upload** for .java files
- **Real-time Code Review** with AI
- **Review History** with search and filter
- **Export Reports** as text files
- **Settings Management** for API keys
- **Responsive Design** for all devices

## 🗄️ Database

- **H2 In-Memory Database** (development)
- **JPA/Hibernate** for data persistence
- **Automatic Schema Creation**
- **Web Console** for database inspection

## 🔐 Security

- **CORS Configuration** for frontend-backend communication
- **Input Validation** on all endpoints
- **API Key Management** (stored securely)
- **Error Handling** with proper HTTP status codes

## 🚀 Deployment

### **Development**
```bash
# Start both servers
node start-both.cjs
```

### **Production**
```bash
# Build backend
cd backend
mvnw.cmd clean package
java -jar target/ai-java-code-reviewer-backend-1.0.0.jar

# Build frontend
cd frontend
npm run build
# Serve build folder with nginx/apache
```

## 🐳 Docker Support

```bash
# Build and run with Docker Compose
docker-compose up --build
```

## 📊 Monitoring

- **Application Logs** in console
- **H2 Console** for database inspection
- **Health Check** endpoints
- **Performance Metrics** via Spring Boot Actuator

## 🔧 Troubleshooting

### **Common Issues:**

1. **Port Already in Use**
   ```bash
   # Kill processes on ports 3000 and 8080
   netstat -ano | findstr :3000
   netstat -ano | findstr :8080
   ```

2. **API Key Not Working**
   - Check environment variables
   - Verify API key format
   - Ensure sufficient credits

3. **CORS Errors**
   - Check backend CORS configuration
   - Verify frontend is running on correct port

4. **Database Connection Issues**
   - Check H2 console access
   - Verify application.properties

## 🎯 Features Comparison

| Feature | Desktop App | Web App |
|---------|-------------|---------|
| **UI Framework** | JavaFX | React |
| **Deployment** | Single JAR | Separate Servers |
| **Access** | Local Only | Web Accessible |
| **Database** | None | H2 Database |
| **History** | Session Only | Persistent |
| **Multi-user** | No | Yes |
| **Mobile** | No | Responsive |

## 📈 Next Steps

- [ ] Add user authentication
- [ ] Implement real-time collaboration
- [ ] Add more programming languages
- [ ] Integrate with GitHub/GitLab
- [ ] Add team management features
- [ ] Implement advanced analytics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test both frontend and backend
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

