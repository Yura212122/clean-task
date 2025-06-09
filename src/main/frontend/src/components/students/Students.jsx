import React, { Fragment, useEffect, useState } from "react";
import { Spinner } from "../spinner/Spinner";
import colors from "../../shared/dto/colors";
import { baseUrl } from "../../api/http-client";
import chevron_left from "../../images/chevron_left.svg";
import next_page from "../../images/next_page.svg";
import endpoints from "../../shared/router/endpoints";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import "./students.css";

export const Students = () => {
  const currentUserRole = useSelector((state) => state.userReducer.user.role);
  const navigate = useNavigate();
  const [students, setStudents] = useState([]);
  const [filteredStudents, setFilteredStudents] = useState([]);
  const [groups, setGroups] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [selectedGroup, setSelectedGroup] = useState("All");
  const [searchTerm, setSearchTerm] = useState("");
  const studentsPerPage = 15;

  useEffect(() => {
    if (
      currentUserRole === "ADMIN" ||
      currentUserRole === "MANAGER" ||
      currentUserRole === "TEACHER" ||
      currentUserRole === "MENTOR"
    ) {
      const fetchStudents = async () => {
        try {
          const response = await baseUrl.get("/api/studentslist");
          const studentsArray = Object.values(response.data).flat();
          setStudents(studentsArray);
          setFilteredStudents(studentsArray);
          setGroups([
            ...new Set(studentsArray.map((student) => student.group)),
          ]);
          setIsLoading(false);
        } catch (error) {
          console.error("Failed to fetch students:", error);
          setIsLoading(false);
        }
      };
      fetchStudents();
    } else if (!currentUserRole) {
      navigate(`/${endpoints.NOT_LOGGED_IN}`);
    } else {
      navigate(`/${endpoints.NOT_FOUND}`);
    }
  }, []);

  useEffect(() => {
    filterStudents();
  }, [selectedGroup, searchTerm]);

  const handlePreviousPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  const handleNextPage = () => {
    if (page < Math.ceil(filteredStudents.length / studentsPerPage) - 1) {
      setPage(page + 1);
    }
  };

  const handleGroupFilter = (group) => {
    setSelectedGroup(group);
    setPage(0);
  };

  const handleSearch = (e) => {
    setSearchTerm(e.currentTarget.value.trim().toLowerCase());
    setPage(0);
  };

  const filterStudents = () => {
    const filtered = students.filter((student) => {
      const matchesGroup =
        selectedGroup === "All" || student.group === selectedGroup;
      const matchesSearch =
        student.id.toString().toLowerCase().includes(searchTerm) ||
        student.name.toLowerCase().includes(searchTerm) ||
        student.surname.toLowerCase().includes(searchTerm) ||
        student.email.toLowerCase().includes(searchTerm) ||
        student.phone.toLowerCase().includes(searchTerm);
      return matchesGroup && matchesSearch;
    });
    setFilteredStudents(filtered);
  };

  const paginatedStudents = filteredStudents.slice(
    page * studentsPerPage,
    (page + 1) * studentsPerPage
  );

  return (
    <Fragment>
  {isLoading ? (
    <Spinner />
  ) : (
    <div
      className="w-100 min-vh-100 d-flex flex-column align-items-center px-2 px-md-4 pt-5"
      style={{ backgroundColor: colors.WHITE }}
    >
      {/* Фільтри */}
      <div className="d-flex flex-column flex-md-row align-items-center justify-content-between w-100 mb-4">
        <div className="mb-2 mb-md-0 fw-semibold">Sort by</div>

        <div className="d-flex flex-column flex-md-row align-items-center w-100 gap-3">
          {/* Фільтр груп */}
          <div className="w-100 w-md-auto">
            <select
              className="form-select"
              onChange={(e) => handleGroupFilter(e.currentTarget.value)}
            >
              <option value="All">All groups</option>
              {groups.map((group) => (
                <option key={group}>{group}</option>
              ))}
            </select>
          </div>

          {/* Пошук */}
          <div className="position-relative w-100 w-md-auto">
            <input
              className="form-control search-input"
              placeholder="Search"
              onKeyUp={handleSearch}
            />
            <div className="position-absolute top-50 end-0 translate-middle-y pe-3">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="20"
                fill="currentColor"
                className="bi bi-search"
                viewBox="0 0 16 16"
              >
                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      {/* Таблиця студентів */}
      <div className="table-responsive w-100">
        <table className="table table-bordered students-table">
          <thead>
            <tr>
              <th>Course</th>
              <th>Group</th>
              <th className="text-center">ID</th>
              <th>Name</th>
              <th>Surname</th>
              <th>Email</th>
              <th>Phone</th>
            </tr>
          </thead>
          <tbody>
            {paginatedStudents.map((student) => (
              <tr key={student.uniqueId}>
                <td>{student.group}</td>
                <td>{student.registerDate}</td>
                <td className="text-center">{student.id}</td>
                <td>{student.name}</td>
                <td>{student.surname}</td>
                <td>{student.email}</td>
                <td>{student.phone}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Пагінація */}
      <div className="d-flex align-items-center justify-content-center gap-3 mt-4">
        <button
          onClick={handlePreviousPage}
          disabled={page === 0}
          className="btn btn-outline-secondary btn-sm btn-pagination"
        >
          <img
            src={chevron_left}
            className="pagination-style-img"
            alt="Previous"
          />
        </button>

        <span className="fw-medium">
          Page {page + 1} of {Math.ceil(filteredStudents.length / studentsPerPage)}
        </span>

        <button
          onClick={handleNextPage}
          disabled={
            page >= Math.ceil(filteredStudents.length / studentsPerPage) - 1
          }
          className="btn btn-outline-secondary btn-sm btn-pagination"
        >
          <img
            src={next_page}
            className="pagination-style-img"
            alt="Next"
          />
        </button>
      </div>
    </div>
  )}
</Fragment>

  );
};
