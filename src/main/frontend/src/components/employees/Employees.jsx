import React, { Fragment, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Spinner } from "../spinner/Spinner";
import { useSelector } from "react-redux";
import chevron_left from "../../images/chevron_left.svg";
import endpoints from "../../shared/router/endpoints";
import colors from "../../shared/dto/colors";
import { baseUrl } from "../../api/http-client";

import "./employees.css";

export const Employees = () => {
  const currentUserRole = useSelector((state) => state.userReducer.user.role);
  const [employees, setEmployees] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    if (
      currentUserRole === "ADMIN" ||
      currentUserRole === "MANAGER" ||
      currentUserRole === "TEACHER" ||
      currentUserRole === "MENTOR"
    ) {
      const fetchEmployees = async () => {
        try {
          const response = await baseUrl.get("/api/allEmployeesList");
          setEmployees(response.data);
          setIsLoading(false);
        } catch (error) {
          console.error("Failed to fetch employees:", error);
          setIsLoading(false);
          // Handle error and navigate to the appropriate page if needed
        }
      };
      fetchEmployees();
    } else if (!currentUserRole) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    } else {
      navigate(`/${endpoints.NOT_FOUND}`);
    }
  }, []);

  const getRoleDisplayName = (role) => {
    switch (role) {
      case "ADMIN":
        return "Admin";
      case "TEACHER":
        return "Teacher";
      case "MANAGER":
        return "Manager";
      case "MENTOR":
        return "Mentor";
    }
  };

  return (
   <>
  {isLoading ? (
    <Spinner />
  ) : (
    <div
      className="w-100 min-vh-100 d-flex flex-column employees-block"
      style={{ backgroundColor: colors.WHITE }}
    >
      <div className="container-fluid">
        <div className="row align-items-center my-3">
          <div className="col-auto">
            <Link to={`/${endpoints.COURSES}`} className="d-inline-block">
              <img
                src={chevron_left}
                className="img-fluid"
                style={{ width: "34px" }}
                alt="Back"
              />
            </Link>
          </div>
          <div className="col">
            <h2 className="fs-4 fs-md-3 fw-bold m-0">ProgAcademy</h2>
          </div>
        </div>
      </div>

      <div className="container-fluid">
        <div className="table-responsive">
          <table className="table table-bordered employees-block-table">
            <thead>
              <tr>
                <th className="employees-block-th-first">ID</th>
                <th className="employees-block-th-other">Name</th>
                <th className="employees-block-th-other">Surname</th>
                <th className="employees-block-th-other">Role</th>
              </tr>
            </thead>
            <tbody>
              {Array.isArray(employees) && employees.length > 0 ? (
                employees.map((employee) => (
                  <tr key={employee.id}>
                    <td className="employees-block-td-first">{employee.id}</td>
                    <td className="employees-block-td-other">{employee.name}</td>
                    <td className="employees-block-td-other">{employee.surname}</td>
                    <td className="employees-block-td-other">
                      {getRoleDisplayName(employee.role)}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" className="text-center">
                    No employees found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )}
</>

  );
};
