import React, { useContext, useEffect, useState } from "react";
import { ReportRequest } from '../../hooks/report-request-provider';
import {Timer} from "../timer/Timer"

import "./form-invite.css";
import colors from "../../shared/dto/colors";
import {useDispatch, useSelector} from "react-redux";
import {removeIsExpired} from "../../redux/slices/isInviteCodeExpiredSlice";

export const FormInvite = () => {
    const { useInviteFormReport } = useContext(ReportRequest);
    const isInviteCodeExpired = useSelector((state) => state.isInviteCodeExpiredReducer.isExpired);
    const dispatch = useDispatch();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const validInviteCode = useInviteFormReport.touched.inviteCode && !useInviteFormReport.errors.inviteCode;
    const invalidInviteCode = useInviteFormReport.touched.inviteCode && useInviteFormReport.errors.inviteCode;
    const disabledButton = !useInviteFormReport.values.inviteCode || useInviteFormReport.errors.inviteCode || localStorage.timer === "true" || isSubmitting;

    useEffect(() => {
        if (useInviteFormReport.values.inviteCode) {
            dispatch(removeIsExpired());
        }
    }, [useInviteFormReport.values.inviteCode, dispatch]);

    const handleSubmitWrapper = async (e) => {
        setIsSubmitting(true);
        try {
            await useInviteFormReport.handleSubmit(e);
        } catch (error) {
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="w-100 d-flex">
            <form
                method="POST"
                className="mx-auto p-2 d-flex flex-column"
                noValidate
                onSubmit={handleSubmitWrapper}
                style={{ width: "304px" }}
            >
                <div
                    className={`d-flex flex-column ${invalidInviteCode && localStorage.date_lock !== "true" ? "mb-0" : ""}`}
                    style={{ marginBottom: "21px" }}
                >
                    {localStorage.timer === "true" ? (
                        <Timer />
                    ) : (
                        <input
                            id="invite-input"
                            type="text"
                            name="inviteCode"
                            placeholder="Input invite code"
                            autoComplete="off"
                            required
                            value={useInviteFormReport.values.inviteCode}
                            onChange={useInviteFormReport.handleChange}
                            onBlur={useInviteFormReport.handleBlur}
                            className="w-100 form-control form-control-sm"
                            style={{
                                color: colors.BLACK,
                                backgroundColor: colors.GRAY_100,
                                borderRadius: "8px",
                                borderColor: `${validInviteCode ? colors.GREEN : invalidInviteCode ? colors.RED : ""}`,
                            }}
                        />
                    )}
                    <label htmlFor="invite-input" className="form-text mt-1 text-center" style={{ color: `${invalidInviteCode ? colors.RED : ""}` }}>
                        {invalidInviteCode && !localStorage.date_lock === true ? useInviteFormReport.errors.inviteCode : ""}
                        {!isInviteCodeExpired ? <p style={{ width: "350px", marginLeft: "-25px",  color: colors.RED }}>Your invite-code has expired! Please, use another one!</p> : ""}
                    </label>
                </div>
                <div className="mb-4 mt-1 d-flex justify-content-center">
                    <button
                        type="submit"
                        className="btn btn-sm text-capitalize"
                        style={{
                            width: "194px",
                            height: "32px",
                            color: `${disabledButton ? colors.GRAY : colors.GREEN_50}`,
                            borderColor: colors.BLUE_900,
                            borderRadius: "8px",
                            backgroundColor: `${disabledButton ? "" : colors.BLUE_900}`,
                        }}
                        disabled={disabledButton}
                        onClick={async (e) => {
                            if (!disabledButton) {
                            await handleSubmitWrapper(e);
                            }
                        }}
                    >
                        {isSubmitting ? "Submitting..." : "Next"}
                    </button>
                </div>
            </form>
        </div>
    );
};