import React, { useEffect } from 'react';
import { Link } from "react-router-dom";
import { NotificationIcon } from '../notification-icon/Notification-icon';
import avatar from "../../images/avatar.svg";
import arrow_down from "../../images/arrow_down.svg";
import icon_telegram from "../../images/icon_telegram.png"

import endpoints from '../../shared/router/endpoints';
import colors from "../../shared/dto/colors";
import "./avatar.css";
import {useSelector} from "react-redux";

export const Avatar = ({ useClickLogout, currentUser,
                         setMainListMenuOpen,
                         isAvatarListMenuOpen,
                         setAvatarListMenuOpen,
                         isNotificationListMenuOpen,
                         setNotificationListMenuOpen, }) => {
  const currentUserRole = useSelector((state) => state.userReducer.user.role);
  const telegramRef = useSelector(state => state.telegramUrlReducer.telegramUrl);

 useEffect(() => {
  const openListMenu = () => document.querySelector("#open-list-menu");
  const closeListMenu = () => document.querySelector("#close-list-menu");
  const avatarListMenu = () => document.querySelector("#avatar-list-menu");

  const openEl = openListMenu();
  const closeEl = closeListMenu();
  const avatarMenu = avatarListMenu();

  const showListMenu = (event) => {
    setAvatarListMenuOpen(true);
    setMainListMenuOpen(false);
    setNotificationListMenuOpen(false);
    event.stopPropagation();
    if (openEl && avatarMenu?.classList.contains("visually-hidden")) {
      avatarMenu.classList.remove("visually-hidden");
      avatarMenu.classList.add("d-block");
      openEl.classList.add("d-none");
      closeEl?.classList.remove("d-none");
    }
  };

  const hideListMenu = (event) => {
    setAvatarListMenuOpen(false);
    setMainListMenuOpen(false);
    setNotificationListMenuOpen(false);
    event.stopPropagation();
    if (openEl && avatarMenu?.classList.contains("d-block")) {
      avatarMenu.classList.remove("d-block");
      avatarMenu.classList.add("visually-hidden");
      openEl.classList.remove("d-none");
      closeEl?.classList.add("d-none");
    }
  };

  if (openEl) openEl.addEventListener("click", showListMenu);
  if (closeEl) closeEl.addEventListener("click", hideListMenu);

  const outsideClickHandler = (event) => {
    if (avatarMenu?.classList.contains("d-block") && isAvatarListMenuOpen) {
      setAvatarListMenuOpen(false);
      avatarMenu.classList.remove("d-block");
      avatarMenu.classList.add("visually-hidden");
      openEl?.classList.remove("d-none");
      closeEl?.classList.add("d-none");
    }
  };

  document.addEventListener("click", outsideClickHandler);

  // 🧹 Clean up
  return () => {
    if (openEl) openEl.removeEventListener("click", showListMenu);
    if (closeEl) closeEl.removeEventListener("click", hideListMenu);
    document.removeEventListener("click", outsideClickHandler);
  };
}, []);

  return (
<nav className="navbar navbar-expand-lg bg-light px-3">
  <ul className="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
    
    {/* Telegram icon */}
    {(currentUserRole === "ADMIN" || currentUserRole === "MANAGER") && (
      <li className="nav-item">
        <span
          onClick={() => window.open(telegramRef, "_blank")}
          className="nav-link cursor-pointer"
          aria-current="page"
        >
          <img src={icon_telegram} width="25" alt="Telegram" />
        </span>
      </li>
    )}

    {/* Notification icon */}
    <li className="nav-item">
      <NotificationIcon
        currentUser={currentUser}
        setMainListMenuOpen={setMainListMenuOpen}
        setAvatarListMenuOpen={setAvatarListMenuOpen}
        isNotificationListMenuOpen={isNotificationListMenuOpen}
        setNotificationListMenuOpen={setNotificationListMenuOpen}
      />
    </li>

    {/* Avatar dropdown */}
    <li className="nav-item dropdown">
      <div
        role="button"
        className={`nav-link d-flex align-items-center rounded-pill px-2 ${isAvatarListMenuOpen ? 'active' : ''}`}
        style={{ backgroundColor: colors.GRAY_100 }}
        onClick={() => setAvatarListMenuOpen(!isAvatarListMenuOpen)}
        aria-haspopup="true"
        aria-expanded={isAvatarListMenuOpen}
      >
        <img
          src={avatar}
          className="rounded-circle"
          width="36"
          height="36"
          alt="Avatar"
        />
        <img
          src={arrow_down}
          className={`ms-2 ${isAvatarListMenuOpen ? 'arrow-rotate' : ''}`}
          width="12"
          height="7"
          alt="Arrow"
        />
      </div>

      <ul
        className={`dropdown-menu dropdown-menu-end mt-2 p-0 shadow-sm ${isAvatarListMenuOpen ? 'show' : ''}`}
        style={{ backgroundColor: colors.GRAY_100 }}
      >
        <li>
          <Link className="dropdown-item" to="#">Settings</Link>
        </li>
        <li>
          <Link className="dropdown-item" to="#">Notifications</Link>
        </li>
        <li>
          <Link className="dropdown-item" to={`/${endpoints.CERTIFICATE}`}>Certifications</Link>
        </li>
        {(currentUserRole === "TEACHER" || currentUserRole === "MENTOR") && (
          <li>
            <Link className="dropdown-item" to={`/${endpoints.TEACHER}`}>Homeworks</Link>
          </li>
        )}
        <li>
          <button className="dropdown-item" onClick={useClickLogout}>
            Logout
          </button>
        </li>
      </ul>
    </li>
  </ul>
</nav>

  );
};