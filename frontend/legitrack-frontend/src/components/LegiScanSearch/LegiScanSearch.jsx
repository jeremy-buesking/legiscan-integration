import React, {useState} from 'react';
import './LegiScanSearch.css';
import axios from 'axios';

function LegiScanSearch() {
    const [query, setQuery] = useState('');
    const [state, setState] = useState('');
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(false);

    const searchBills = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await axios.get('http://localhost:8080/api/legiscan/search', {
                params: {
                    query: query,
                    state: state
                }
            });
            setBills(response.data.searchresult);
            console.log("Bills is an array:", Array.isArray(bills));
        } catch (error) {
            console.error('Error fetching bills:', error);
        }
        setLoading(false);
    };

    return (
        <div>
            <form onSubmit={searchBills}>
                <div>
                    <label>Search Query:</label>
                    <input
                        type="text"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        placeholder="Enter search terms"
                    />
                </div>
                <div>
                    <label>State:</label>
                    <input
                        type="text"
                        value={state}
                        onChange={(e) => setState(e.target.value)}
                        placeholder="Enter state code (e.g., CA)"
                    />
                </div>
                <button type="submit">Search Bills</button>
            </form>

            {loading && <p>Loading...</p>}

            <div>
                {bills && Object.keys(bills).length > 0 ? (
                    Object.values(bills).map((bill) => (
                        <div className="bill-info-card" key={bill.bill_id}>
                            <h1 className="bill-title">{bill.title}</h1>
                            <p className="bill-number">Bill Number: {bill.bill_number}</p>
                            <p className="state">State: {bill.state}</p>
                            <p className="last-action">Last Action:<br/> {bill.last_action}<br/> on:<br/> {bill.last_action_date}</p>
                            <a className="read-more" href={bill.url} target="_blank" rel="noopener noreferrer">Read
                                More</a>
                        </div>
                    ))
                ) : (
                    <p>No bills found.</p>
                )}
            </div>
        </div>
    );
}

export default LegiScanSearch;