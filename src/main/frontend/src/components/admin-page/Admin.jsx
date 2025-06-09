import React, { useState } from 'react';
import { useForm } from 'react-hook-form';

const AdminBroadcast = () => {
    const [selectedAdmin, setSelectedAdmin] = useState(null);
    const [broadcastMessage, setBroadcastMessage] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const { register, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            emailSearch: '',
        },
    });

    console.log("Component rendered");

    // Handle email search (GET request)
    const handleSearch = async (data) => { // Changed from (e) to (data)
        console.log("handleSearch triggered with data:", data);
        setError('');
        setSuccess('');

        if (!data.emailSearch.trim()) {
            setError('Email should not be empty');
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8080/api/admin/findByEmail?email=${encodeURIComponent(data.emailSearch)}`
            );

            if (!response.ok) {
                if (response.status === 400) {
                    setError('Email should not be empty');
                }
                throw new Error('Failed to fetch admin data');
            }

            const responseData = await response.json();
            if (responseData.length > 0) {
                setSelectedAdmin(responseData[0]);
                setError('');
            } else {
                setError('No admins found with this email');
                setSelectedAdmin(null);
            }
        } catch (err) {
            setError(err.message || 'Failed to fetch admin data');
            setSelectedAdmin(null);
        }
    };

    // Handle broadcast message submission (POST request)
    const handleBroadcast = async (e) => {
        setError('');
        setSuccess('');

        if (!broadcastMessage.trim()) {
            setError('Message cannot be empty');
            return;
        }

        if (!selectedAdmin) {
            setError('Please select an admin first');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/admin/broadcast', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    adminEmail: selectedAdmin.email,
                    adminName: `${selectedAdmin.name} ${selectedAdmin.surname}`,
                    message: broadcastMessage,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to send broadcast');
            }

            setSuccess('Message broadcasted successfully');
            setBroadcastMessage('');
        } catch (err) {
            setError('Failed to broadcast message');
        }
    };

    const handleButtonClick = (e) => {
        e.stopPropagation();
        console.log("Button clicked");
        // Workaround: Manually trigger form submission if needed
        const form = e.currentTarget.closest('form');
        if (form && !e.defaultPrevented) {
            form.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        }
    };

    return (
       <div className="container mt-4">
  {/* Search Admin Section */}
  <div className="card mb-4">
    <div className="card-body">
      <form onSubmit={handleSubmit(handleSearch)}>
        <div className="mb-3">
          <label htmlFor="emailSearch" className="form-label">
            Search Admin by Email:
          </label>
          <div className="input-group">
            <input
              type="email"
              className={`form-control ${errors.emailSearch ? 'is-invalid' : ''}`}
              id="emailSearch"
              placeholder="Enter admin email"
              {...register('emailSearch', {
                required: 'Email is required',
              })}
            />
            <button
              className="btn btn-primary"
              type="submit"
              onClick={handleButtonClick}
            >
              Search
            </button>
          </div>
          {errors.emailSearch && (
            <div className="invalid-feedback d-block">
              {errors.emailSearch.message}
            </div>
          )}
        </div>
      </form>

      {selectedAdmin && (
        <div className="mt-4">
          <h6 className="mb-2">Selected Admin:</h6>
          <ul className="list-group">
            <li className="list-group-item">
              <strong>Name:</strong> {selectedAdmin.name} {selectedAdmin.surname}
            </li>
            <li className="list-group-item">
              <strong>Email:</strong> {selectedAdmin.email}
            </li>
          </ul>
        </div>
      )}
    </div>
  </div>

  {/* Broadcast Section */}
  {selectedAdmin && (
    <div className="card mb-4">
      <div className="card-body">
        <h5 className="card-title">Broadcast Message</h5>
        <form onSubmit={handleSubmit(handleBroadcast)}>
          <div className="mb-3">
            <label htmlFor="broadcastMessage" className="form-label">
              Message:
            </label>
            <textarea
              className="form-control"
              id="broadcastMessage"
              value={broadcastMessage}
              onChange={(e) => setBroadcastMessage(e.target.value)}
              placeholder="Type your broadcast message here"
              rows="4"
            />
          </div>
          <button
            className="btn btn-success"
            type="submit"
            onClick={handleButtonClick}
          >
            Send Broadcast
          </button>
        </form>
      </div>
    </div>
  )}

  {/* Status Messages */}
  {error && (
    <div className="alert alert-danger" role="alert">
      {error}
    </div>
  )}
  {success && (
    <div className="alert alert-success" role="alert">
      {success}
    </div>
  )}
</div>

    );
};

export default AdminBroadcast;