import { baseUrl } from "../api/http-client";
import { useEffect, useState } from "react";

export const useGetLessons = (id) => {
    const [data, setData] = useState([]);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const sessionId = localStorage.getItem('sessionId');
                const { data } = await baseUrl.get(`/api/courses/${id}/lessons`, {
                    headers: {
                        "Authorization": `${sessionId}`
                    }
                });
                setData(data);
            } catch (error) {
                setError(error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [id])

    return { data, error, isLoading }
}