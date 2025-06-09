import React, { useEffect, useState } from 'react';

import "./progress.css";

export const Progress = () => {
    const [isValue, setValue] = useState(100);

    useEffect(() => {
        let index = 100;
        let max = 300;
        function showProgressBar() {
            index++;
            if (index <= max) {
                setValue(index);
                setTimeout(showProgressBar, 300);
            }
        }
        showProgressBar();

    }, [])

    return (
        <div className="w-100 d-flex position-fixed top-0 start-0">
            <progress className="w-100" style={{ height: "4px" }} value={isValue} max="300" />
        </div>
    )
};
