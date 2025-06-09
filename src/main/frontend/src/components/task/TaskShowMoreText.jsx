import React, { useState } from 'react';

const TaskShowMoreText = ({ text, maxLength }) => {
    const [isExpanded, setIsExpanded] = useState(false);
    const toggleIsExpanded = () => {
        setIsExpanded(!isExpanded);
    };

    return (
        <span>
            <span className="text-black">
                {isExpanded ? text : text.slice(0, maxLength) + (text.length > maxLength ? '...' : '')}
            </span><br />
            {text.length > maxLength && (
                <div className="text-center mt-2">
                    <button
                        onClick={toggleIsExpanded}
                        className="btn btn-sm btn-outline-success m-auto"
                        style={{ transform: "scale(0.8)", marginLeft: "120px"}}>
                        {isExpanded ? 'Show less' : 'Show more'}
                    </button>
                </div>
            )}
        </span>
    );
};

export default TaskShowMoreText;