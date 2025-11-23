import { render, screen } from '@testing-library/react';
import App from './App';
import '@testing-library/jest-dom';

test('renders ZenGame welcome message', () => {
  render(<App />);
  const heading = screen.getByText(/добро пожаловать в ZenGame/i);
  expect(heading).toBeInTheDocument();
});
