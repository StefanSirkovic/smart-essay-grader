import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import type { EssayResponse } from '../types';
import StatusBadge from '../components/StatusBadge';
import { FileText, PlusCircle, Loader2, AlertCircle } from 'lucide-react';

export default function EssaysPage() {
  const [essays, setEssays] = useState<EssayResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .get<EssayResponse[]>('/essays')
      .then((res) => setEssays(res.data))
      .catch(() => setError('Failed to load essays'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-8 h-8 text-primary-500 animate-spin" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64 text-red-500 gap-2">
        <AlertCircle className="w-5 h-5" />
        {error}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Essays</h1>
          <p className="text-gray-500 mt-1">
            {essays.length} essay{essays.length !== 1 ? 's' : ''} total
          </p>
        </div>
        <Link
          to="/essays/new"
          className="inline-flex items-center gap-2 px-4 py-2.5 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition text-sm"
        >
          <PlusCircle className="w-4 h-4" />
          New Essay
        </Link>
      </div>

      {essays.length === 0 ? (
        <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
          <FileText className="w-12 h-12 mx-auto mb-4 text-gray-300" />
          <h3 className="text-lg font-medium text-gray-900">No essays yet</h3>
          <p className="text-gray-500 mt-1 mb-4">
            Submit your first essay and get AI-powered feedback.
          </p>
          <Link
            to="/essays/new"
            className="inline-flex items-center gap-2 px-4 py-2.5 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition text-sm"
          >
            <PlusCircle className="w-4 h-4" />
            Create Essay
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200 bg-gray-50">
                <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Title
                </th>
                <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider hidden sm:table-cell">
                  Date
                </th>
                <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="text-right px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Score
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {essays.map((essay) => (
                <tr
                  key={essay.id}
                  className="hover:bg-gray-50 transition-colors"
                >
                  <td className="px-6 py-4">
                    <Link
                      to={`/essays/${essay.id}`}
                      className="font-medium text-gray-900 hover:text-primary-600 transition-colors"
                    >
                      {essay.title}
                    </Link>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500 hidden sm:table-cell">
                    {new Date(essay.submittedAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4">
                    <StatusBadge status={essay.status} />
                  </td>
                  <td className="px-6 py-4 text-right text-sm font-semibold text-gray-900">
                    {essay.score != null ? `${essay.score}/100` : '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
