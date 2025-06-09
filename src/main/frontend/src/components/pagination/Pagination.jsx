import React from 'react';

import "./pagination.css";

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    return (
    <div className="d-flex justify-content-center gap-2 mt-3">
  {pages.map((page) => (
    <button
      key={page}
      className={`btn btn-sm ${currentPage === page ? "btn-primary activePage" : "btn-outline-secondary noActivePage"}`}
      onClick={() => onPageChange(page)}
      disabled={currentPage === page}
    >
      {page + 1}
    </button>
  ))}
</div>

    );
};

export default Pagination;