import { useState } from "react";
import "./App.css";

function App() {

  const [file, setFile] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    dateOfBirth: "",
    gender: "",
    identityNumber: "",
    address: ""
  });

  const [loading, setLoading] = useState(false);


  // =========================================
  // INPUT CHANGE
  // =========================================

  const handleChange = (event) => {

    const { name, value } =
      event.target;

    setFormData({
      ...formData,
      [name]: value
    });
  };


  // =========================================
  // FILE SELECT
  // =========================================

  const handleFileChange = (event) => {

    const selectedFile =
      event.target.files[0];

    setFile(selectedFile);
  };


  // =========================================
  // SCAN IDENTITY
  // =========================================

  const scanIdentity = async () => {

    if (!file) {

      alert(
        "Please select an identity image"
      );

      return;
    }


    setLoading(true);


    const formDataToSend =
      new FormData();

    formDataToSend.append(
      "file",
      file
    );


    try {

      const response =
        await fetch(
          "http://localhost:8080/api/identity/scan",
          {
            method: "POST",
            body: formDataToSend
          }
        );


      if (!response.ok) {

        throw new Error(
          "OCR failed"
        );
      }


      const data =
        await response.json();


      console.log(
        "Extracted Data:",
        data
      );


      // =================================
      // AUTO FILL FORM
      // =================================

      setFormData({

        name:
          data.name || "",

        dateOfBirth:
          data.dateOfBirth || "",

        gender:
          data.gender || "",

        identityNumber:
          data.identityNumber || "",

        address:
          data.address || ""

      });


    } catch (error) {

      console.error(error);

      alert(
        "Unable to scan the identity image"
      );

    } finally {

      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevents page reload

    const formData = {
        name: name,             // State variable for Name input
        dateOfBirth: dob,       // State variable for DOB input
        gender: gender,         // State variable for Gender input
        identityNumber: idNum,  // State variable for Identity Number input
        address: address        // State variable for Address input
    };

    try {
        const response = await fetch('http://localhost:8080/api/identity/submit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        });

        if (response.ok) {
            const message = await response.text();
            alert("Success: " + message);
        } else {
            alert("Failed to submit identity data.");
        }
    } catch (error) {
        console.error("Error submitting form:", error);
        alert("Network error while submitting data.");
    }
};


  return (

    <div className="container">

      <h1>
        Identity OCR Application
      </h1>


      {/* =============================== */}
      {/* IMAGE UPLOAD */}
      {/* =============================== */}

      <div className="card">

        <h2>
          Scan Identity
        </h2>


        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
        />


        {file && (

          <p className="filename">
            Selected file: {file.name}
          </p>

        )}


        <button
          onClick={scanIdentity}
          disabled={loading}
        >

          {loading
            ? "Scanning..."
            : "Scan Identity"}

        </button>

      </div>


      {/* =============================== */}
      {/* FORM */}
      {/* =============================== */}

      <div className="card">
        
        <h2>
          Personal Information
        </h2>


        {/* NAME */}

        <label>
          Name
        </label>

        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Enter name"
        />


        {/* DOB */}

        <label>
          Date of Birth
        </label>

        <input
          type="text"
          name="dateOfBirth"
          value={formData.dateOfBirth}
          onChange={handleChange}
          placeholder="DD/MM/YYYY"
        />


        {/* GENDER */}

        <label>
          Gender
        </label>

        <input
          type="text"
          name="gender"
          value={formData.gender}
          onChange={handleChange}
          placeholder="Gender"
        />


        {/* ID NUMBER */}

        <label>
          Identity Number
        </label>

        <input
          type="text"
          name="identityNumber"
          value={formData.identityNumber}
          onChange={handleChange}
          placeholder="Identity Number"
        />


        {/* ADDRESS */}

        <label>
          Address
        </label>

        <textarea
          name="address"
          value={formData.address}
          onChange={handleChange}
          placeholder="Address"
          rows="5"
        />

        <button type='submit' onClick={handleSubmit}>SUBMIT</button>

      </div>

    </div>
  );
}

export default App;