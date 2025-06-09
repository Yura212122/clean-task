import React, {Fragment, useEffect, useState} from 'react';
import {UserCertificates} from '../user-certificates/User-certificates';
import "./form-certificate.css";
import colors from "../../shared/dto/colors";
import {useSelector} from "react-redux";
import {NotUser} from "../not-user/not-user";
import {baseUrl} from "../../api/http-client";
import graduate from "../../images/graduate.svg";
import {Spinner} from "../spinner/Spinner";

export const FormCertificate = () => {
    const currentUser = useSelector((state) => state.userReducer.user);
    const [certificateData, setCertificateData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedUniqueId, setSelectedUniqueId] = useState(null);

    useEffect(() => {
        const fetchCertificates = async () => {
            try {
                const response = await baseUrl.get(`/api/certificate/user/${currentUser.id}`);
                const data = response.data;
                console.log('Fetched certificates:', data);
                setCertificateData(data);
            } catch (error) {
                console.error('Error fetching certificates:', error);
                setError(new Error(`Failed to fetch certificates: ${error.message}`));
            } finally {
                setLoading(false);
            }
        };

        if (currentUser.id) {
            fetchCertificates();
        }
    }, [currentUser.id]);

    if (!currentUser.id) {
        return <NotUser/>;
    }

    if (loading) {
        return <Spinner/>;
    }

    if (error) {
        return <div>Error: {error.message}</div>;
    }

    const handleButtonClick = (uniqueId) => {
        console.log('Clicked UniqueId:', uniqueId);
        setSelectedUniqueId(uniqueId);
    };

    return (
        <Fragment>
            {certificateData.length > 0 ?
                <div className='w-100 mt-5'>
                    {selectedUniqueId ? (
                        <UserCertificates uniqueId={selectedUniqueId}/>
                    ) : (
                        <section className="w-100 d-flex flex-column position-relative mt-5 mb-5 form-certificate">
                            <div className="mb-4 d-flex justify-content-center image-from-md mt-5">
                                <figure className="m-0 d-flex">
                                    <img src={graduate} alt="Graduate" className="img-fluid img-to-sm" width={250}
                                         height={250}/>
                                </figure>
                            </div>
                            <h1 className="w-100 mb-5 text-center fs-2 fw-bold">Your certificates</h1>

                            {certificateData.map((certificate) => (
                                <div key={certificate.id} className="d-flex align-items-center justify-content-center my-2">
                                    <div className="certificate-number">{certificate.id}.</div>
                                    <div className="certificate-name">{certificate.groupName}</div>
                                    <button onClick={() => handleButtonClick(certificate.uniqueId)}
                                            className="btn btn-sm button-width"
                                            style={{
                                                height: "32px",
                                                color: colors.GREEN_50,
                                                borderColor: colors.BLUE_900,
                                                borderRadius: "8px",
                                                backgroundColor: colors.BLUE_900
                                            }}
                                    >View Certificates
                                    </button>
                                    <hr/>
                                </div>
                            ))}
                        </section>
                    )}
                </div> :
                <div className="w-100 d-flex justify-content-center align-items-center">
                    <p style={{fontSize: "1.5rem", textAlign: "center"}}>You don't have any certificates yet, but everything is ahead.</p>
                </div>
            }
        </Fragment>
    );
};