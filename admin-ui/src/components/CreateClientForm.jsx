import { useState } from "react";

export default function CreateClientForm({ onSubmit }) {
  const [clientId, setClientId] = useState("");
  const [clientSecret, setClientSecret] = useState("");

  const handle = (e) => {
    e.preventDefault();
    onSubmit({ clientId, clientSecret, grantTypes: ["CLIENT_CREDENTIALS"], scopes: ["openid","profile"] });
    setClientId(""); setClientSecret("");
  };

  return (
    <form onSubmit={handle} className="p-4 border rounded mb-4 space-y-3">
      <div>
        <label className="block mb-1 font-medium">Client ID</label>
        <input className="border p-2 w-full" value={clientId} onChange={(e) => setClientId(e.target.value)} />
      </div>
      <div>
        <label className="block mb-1 font-medium">Client Secret</label>
        <input className="border p-2 w-full" value={clientSecret} onChange={(e) => setClientSecret(e.target.value)} />
      </div>
      <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded">Create Client</button>
    </form>
  );
}