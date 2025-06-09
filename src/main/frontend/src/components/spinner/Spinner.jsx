import React from 'react';

import "./spinner.css";

export const Spinner = () => {
    return (
        <div className='w-100 d-flex justify-content-center align-items-center'>
            <div id='spinner' className="d-flex"></div>
        </div>
    )
};
