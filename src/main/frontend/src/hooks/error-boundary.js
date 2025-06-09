import React from "react";
import { Link } from "react-router-dom";

export class ErrorBoundary extends React.Component {
    state = {
        hasError: false,
    };
    componentDidCatch() {
        return this.setState({ hasError: true });
    };
    render() {
        if (this.state.hasError) {
            return <main className="container-fluid d-flex flex-grow-1 justify-content-center">
                <div className="w-100">
                    <p className='w-100 mt-4 fs-5 text-danger'>
                        Something went wrong! Go to&nbsp;
                        <Link to={"/"} reloadDocument className="text-decoration-none fs-5">home</Link>
                    </p>
                </div>
            </main>
        }
        return this.props.children;
    };
};