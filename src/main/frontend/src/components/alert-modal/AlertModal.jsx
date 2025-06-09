import { Fragment } from "react";
import { useNavigate } from "react-router-dom";
import colors from "../../shared/dto/colors";

export const AlertModal = ({
  isOpen,
  onClose,
  title,
  alertText,
  buttonText,
  navigateTo,
  onNavigate,
}) => {
  const navigate = useNavigate();

  if (!isOpen) return null;

  const handleButtonClick = () => {
    if (navigateTo) {
      navigate(navigateTo);
    } else if (onNavigate) {
      onNavigate();
    }
    onClose();
  };

  return (
   <Fragment>
  <div
    className="modal fade show"
    style={{ display: 'block', backgroundColor: 'rgba(0, 0, 0, 0.5)' }}
    tabIndex="-1"
    role="dialog"
    onClick={onClose}
  >
    <div
      className="modal-dialog modal-dialog-centered"
      role="document"
      onClick={(e) => e.stopPropagation()} // prevent modal close on content click
    >
      <div className="modal-content text-center">
        <div className="modal-body">
          <p className="fw-bold mb-2" style={{ fontSize: '24px', color: colors.BLUE_900 }}>
            {title}
          </p>
          <p className="fw-bold mb-3" style={{ fontSize: '20px', color: colors.BLUE_900 }}>
            {alertText}
          </p>
          <button
            type="button"
            className="btn btn-primary"
            style={{
              color: colors.GREEN_50,
              backgroundColor: colors.BLUE_500,
              borderColor: colors.BLUE_500,
            }}
            onClick={handleButtonClick}
          >
            {buttonText}
          </button>
        </div>
      </div>
    </div>
  </div>
</Fragment>

  );
};
