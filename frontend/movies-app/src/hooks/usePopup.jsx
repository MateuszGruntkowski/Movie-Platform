import { useCallback, useEffect, useRef, useState } from "react";

const POPUP_DURATION_MS = 3000;
const HIDDEN = { show: false, message: "", type: "" };

export const usePopup = () => {
  const [popup, setPopup] = useState(HIDDEN);
  const timerRef = useRef(null);

  const hidePopup = useCallback(() => {
    clearTimeout(timerRef.current);
    setPopup(HIDDEN);
  }, []);

  const showPopup = useCallback((message, type) => {
    clearTimeout(timerRef.current);
    setPopup({ show: true, message, type });
    timerRef.current = setTimeout(() => setPopup(HIDDEN), POPUP_DURATION_MS);
  }, []);

  useEffect(() => () => clearTimeout(timerRef.current), []);

  return {
    popup,
    showPopup,
    hidePopup,
  };
};