import LegiScanSearch from './components/LegiScanSearch/LegiScanSearch';
import MasterListSearch from './components/MasterListSearch/MasterListSearch';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <LegiScanSearch />
        <MasterListSearch />
      </header>
    </div>
  );
}

export default App;
