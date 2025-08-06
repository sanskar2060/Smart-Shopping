import { useState } from 'react'
import Home from './components/Home/Home'
import Login from "./components/Auth/Login"
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Register from './components/Auth/Register';
import { AuthProvider } from './components/Context/AuthContext';
import  ProtectedRoute from "./components/Context/ProtectedRoute"
import CompareNow from "./components/Pages/CompareNow"

import HowItWorks from './components/Products/HowItWorks';
import SupportedStores from './components/Pages/SupportedStores';
function App() {
  const [count, setCount] = useState(0)

  return (
    <>
    <AuthProvider>
   <Router>
      <Routes>
         <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
<Route path="/search" element={<CompareNow />} />
<Route path="/how-it-works" element={<HowItWorks />} />
<Route path="/stores" element={<SupportedStores />} />

<Route element={<ProtectedRoute />}>
       //ProtectedRoute will we here
        </Route>
      </Routes>
    </Router>
    </AuthProvider>
    </>
  )
}

export default App
