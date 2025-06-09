import { baseUrl } from "../api/http-client";
import { useEffect, useState } from "react";
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

import endpoints from "../shared/router/endpoints";

export const useGetLessonDetails = (lessonId) => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const currentUser = useSelector((state) => state.userReducer.user);
    const currentUserId = currentUser.id;
    const navigate = useNavigate();

    useEffect(() => {
        if(currentUserId) {
            const fetchData = async () => {
                try {
                    const { data } = await baseUrl.get(`/api/lessons/${lessonId}`, {
                        params: { currentUserId }
                    });
                    setData(data);
                } catch (error) {
                    setError(error);
                    navigate(`/${endpoints.NOT_FOUND}`);
                } finally {
                    setIsLoading(false);
                }
            };
            fetchData();
        } else {
            navigate(`/${endpoints.NOT_LOGGED_IN}`);
        }

    }, [lessonId]);

    return { data, error, isLoading };
};