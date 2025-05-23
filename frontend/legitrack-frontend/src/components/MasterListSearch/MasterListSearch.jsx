import React, {useState} from 'react';
import './MasterListSearch.css';
import axios from 'axios';

function MasterListSearch() {
    const [state, setState] = useState('');
    const [masterBillList, setMasterBillList] = useState([]);

    const getMasterList = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.get('http://localhost:8080/api/legiscan/masterList', {
                params: {
                    state: state
                }
            });
            setMasterBillList(response.data.searchresult);
            console.log("masterBillList is an array: ", Array.isArray(masterBillList));
        }catch (error) {
            console.error('Error fetching master list: ', error);
        }
    };

    return (
        <div>
            <form onSubmit={getMasterList}>
                <div>
                    <label>Select State for a Session Master List: </label>
                    <input
                        type="text"
                        value={state}
                        onChange={(e) => setState(e.target.value)}
                        placeholder="Enter state code (e.g., CA)"/>
                </div>
                <button type="submit">Search</button>
            </form>
            <div>
                {masterBillList && Object.keys(masterBillList).length > 0 ? (
                    Object.values(masterBillList).map((bill) => (
                        <div>

                        </div>
                    ))
                ) : (
                    <p>No List Found.</p>
                )}
            </div>
        </div>
    );
}

export default MasterListSearch;