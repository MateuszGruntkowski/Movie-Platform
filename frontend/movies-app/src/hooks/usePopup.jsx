import { useState } from "react";

export const usePopup = () => {
  const [popup, setPopup] = useState({ show: false, message: "", type: "" });

  const showPopup = (message, type) => {
    setPopup({ show: true, message, type });
    setTimeout(() => {
      setPopup({ show: false, message: "", type: "" });
    }, 3000);
  };

  const hidePopup = () => {
    setPopup({ show: false, message: "", type: "" });
  };

  return {
    popup,
    showPopup,
    hidePopup,
  };
};
