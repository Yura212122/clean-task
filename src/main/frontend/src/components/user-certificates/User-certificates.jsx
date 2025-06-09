import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import { useSelector } from "react-redux";
import { NotUser } from "../not-user/not-user";
import { baseUrl } from "../../api/http-client";
import {Spinner} from "../spinner/Spinner";
import "./user-certificates.css";

const ALT_USER_CERTIFICATE = "Certificate";

export const UserCertificates = ({ uniqueId }) => {
    const currentUser = useSelector((state) => state.userReducer.user);
    const [certificateData, setCertificateData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCertificate = async () => {
            try {
                const response = await baseUrl.get(`/api/certificate/${uniqueId}`);
                console.log('Fetched certificate data:', response.data);
                setCertificateData(response.data);
            } catch (error) {
                console.error('Error fetching certificate:', error);
                setError(new Error(`Failed to fetch certificate: ${error.message}`));
            } finally {
                setLoading(false);
            }
        };

        if (uniqueId) {
            fetchCertificate();
        }
    }, [uniqueId]);

    if (!currentUser.id) {
        return <NotUser />;
    }

    if (loading) {
        return <div style={{marginTop: "6rem"}}><Spinner /></div>;
    }

    if (error) {
        return <div>Error: {error.message}</div>;
    }

    const onHandleActivate = () => {
        const link = document.createElement('a');
        link.href = `data:image/png;base64,${certificateData}`;
        link.download = "certificate_of_graduation.png";
        link.click();
    };

    const reloadPage = () => {
        window.location.reload();
    };

    return (
        <Container className="m-0 mt-2 p-0 d-flex justify-content-center">
            <figure className="m-0 position-relative col-12 col-sm-12 col-md-12 col-lg-9 col-xl-7">
                <figcaption className='mt-5 fs-3 text-center text-uppercase fw-semibold'>
                    Certificate of Graduation
                </figcaption>
                <div className="d-flex justify-content-center">
                    <img
                        src={`data:image/png;base64,${certificateData}`}
                        alt={ALT_USER_CERTIFICATE}
                        className="img-fluid"
                    />
                </div>
                <div className="mt-2 d-flex justify-content-around">
                    <button onClick={reloadPage} className="btn btn-lg btn-dark">
                        <div className="d-flex justify-content-center align-items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                                 className="bi icon-size" viewBox="5 0 16 16">
                                <path fill-rule="evenodd"
                                      d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0"/>
                            </svg>
                            <div className="back-button">Back to certificates list</div>
                        </div>
                    </button>
                    <button onClick={onHandleActivate} className="btn btn-lg btn-dark">
                        <div className="download-button">Download Certificate</div>
                    </button>
                </div>
            </figure>
        </Container>
    );
};