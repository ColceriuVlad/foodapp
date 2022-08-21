import './App.scss';

import HomePage from './components/home-page/home-page';
import {
  Routes,
  Route,
  Navigate
} from "react-router-dom";
import ResponsiveAppBar from './components/layout/responsive-app-bar/responsive-app-bar';
import React from 'react';
import SuppliersList from './components/suppliers-list/suppliers-list';
import UserProfile from './components/user-profile/user-profile';
import AdminPage from './components/admin-page/admin-page';
import SupplierPage from './components/supplier-page/supplier-page';
import AuthForm from './components/auth-form/auth-form';
import { Container } from '@mui/material';
import { useSelector } from 'react-redux';
import { RootState } from './store';

function App() {
  const isLoggedIn = useSelector((state: RootState) => state.auth.isLoggedIn);
  return (
    <React.Fragment>
      <ResponsiveAppBar />
      <Container maxWidth="lg">
        <Routes>
          <Route path="/login" element={<AuthForm />} />

          {isLoggedIn &&
            <Route path="/" element={<HomePage />} />}

          {isLoggedIn &&
            <Route path="suppliers" element={<SuppliersList />} >
              <Route path=":supplierId" element={<SupplierPage />} />
            </Route>}

          {isLoggedIn &&
            <Route path="user-profile" element={<UserProfile />} />}

          {isLoggedIn &&
            <Route path="admin-page" element={<AdminPage />} />}

          <Route
            path="*"
            element={
              <Navigate to="/login" replace />
            }
          />
        </Routes>
      </Container>

    </React.Fragment>

  );
}

export default App;
