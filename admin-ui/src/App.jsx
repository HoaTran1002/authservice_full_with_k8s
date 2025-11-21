import { useEffect, useState } from "react";
import { getClients, createClient } from "./api";
import ClientTable from "./components/ClientTable";
import CreateClientForm from "./components/CreateClientForm";

export default function App() {
  const [clients, setClients] = useState([]);

  const load = async () => {
    const data = await getClients();
    setClients(data);
  };

  const handleCreate = async (payload) => {
    await createClient(payload);
    await load();
  };

  useEffect(() => { load(); }, []);

  return (
    <div className="p-8 max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Registered Clients Admin</h1>
      <CreateClientForm onSubmit={handleCreate} />
      <h2 className="text-lg font-semibold mb-2">Existing Clients</h2>
      <ClientTable clients={clients} />
    </div>
  );
}