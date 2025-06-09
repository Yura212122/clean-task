import React from 'react';
import { Link } from 'react-router-dom';
import telegram from "../../images/telegram.svg";

export const Footer = () => {
    return (
        <footer className="w-100">
            <hr className="m-0 border-danger" />
            <div className="d-flex align-items-center py-2">
                <div className="d-flex">
                    <Link to={`https://core.telegram.org/`} target="_blank" aria-label="telegram">
                        <img src={telegram} alt="Telegram" width={28} height={24} />
                    </Link>
                </div>
            </div>
        </footer>
    )
};
