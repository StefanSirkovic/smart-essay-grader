import type { EssayStatus } from '../types';

const config: Record<EssayStatus, { label: string; classes: string }> = {
  SUBMITTED: {
    label: 'Submitted',
    classes: 'bg-blue-100 text-blue-700',
  },
  GRADING: {
    label: 'Grading...',
    classes: 'bg-yellow-100 text-yellow-700 animate-pulse',
  },
  GRADED: {
    label: 'Graded',
    classes: 'bg-green-100 text-green-700',
  },
  FAILED: {
    label: 'Failed',
    classes: 'bg-red-100 text-red-700',
  },
};

export default function StatusBadge({ status }: { status: EssayStatus }) {
  const { label, classes } = config[status];
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${classes}`}>
      {label}
    </span>
  );
}
